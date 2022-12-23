package com.medicalassistance.core.service;

import com.medicalassistance.core.common.PatientRecordStatus;
import com.medicalassistance.core.common.UserCommonService;
import com.medicalassistance.core.entity.AssignedPatient;
import com.medicalassistance.core.entity.DoctorAppointment;
import com.medicalassistance.core.entity.PatientRecord;
import com.medicalassistance.core.entity.User;
import com.medicalassistance.core.exception.AlreadyExistsException;
import com.medicalassistance.core.exception.InvalidUserRequestException;
import com.medicalassistance.core.exception.ResourceNotFoundException;
import com.medicalassistance.core.mapper.AppointmentMapper;
import com.medicalassistance.core.mapper.AssignedPatientMapper;
import com.medicalassistance.core.mapper.UserMapper;
import com.medicalassistance.core.repository.AssignedPatientRepository;
import com.medicalassistance.core.repository.DoctorAppointmentRepository;
import com.medicalassistance.core.repository.PatientRecordRepository;
import com.medicalassistance.core.repository.UserRepository;
import com.medicalassistance.core.request.AppointmentListForDateRequest;
import com.medicalassistance.core.request.AppointmentRequest;
import com.medicalassistance.core.response.AppointmentListForDateResponse;
import com.medicalassistance.core.response.AppointmentResponse;
import com.medicalassistance.core.response.AssignedPatientResponse;
import com.medicalassistance.core.response.PatientRecordResponse;
import com.medicalassistance.core.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class DoctorService {
    @Autowired
    UserCommonService userCommonService;

    @Autowired
    DoctorAppointmentRepository appointmentRepository;

    @Autowired
    AppointmentMapper appointmentMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PatientRecordService patientRecordService;

    @Autowired
    AssignedPatientRepository assignedPatientRepository;

    @Autowired
    AssignedPatientMapper assignedPatientMapper;

    @Autowired
    PatientRecordRepository patientRecordRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PatientService patientService;

    public void storeDoctorAppointment(AppointmentRequest appointmentRequest) {
        String doctorId = userCommonService.getUser().getUserId();
        ZonedDateTime nowZonedDateTime = TimeUtil.nowUTC();
        if (appointmentRequest.getStartDateTime().isBefore(nowZonedDateTime) ||
                appointmentRequest.getStartDateTime().isEqual(nowZonedDateTime) ||
                appointmentRequest.getStartDateTime().isAfter(appointmentRequest.getEndDateTime()) ||
                appointmentRequest.getStartDateTime().isEqual(appointmentRequest.getEndDateTime())) {
            throw new InvalidUserRequestException("appointment time period invalid");
        }
        if (!patientRecordRepository.existsByPatientRecordId(appointmentRequest.getPatientRecordId())) {
            throw new ResourceNotFoundException(String.format("patient record %s not found", appointmentRequest.getPatientRecordId()));
        }
        if (appointmentRepository.existsByPatientRecordId(appointmentRequest.getPatientRecordId())) {
            throw new AlreadyExistsException("patient already has reserved timeslot");
        }
        if (appointmentRepository.existsByDoctorIdAndStartDateTimeBetweenOrStartDateTimeEquals(
                doctorId,
                appointmentRequest.getStartDateTime(), appointmentRequest.getEndDateTime(),
                appointmentRequest.getStartDateTime()) ||
                appointmentRepository.existsByDoctorIdAndEndDateTimeBetweenOrEndDateTimeEquals(
                        doctorId,
                        appointmentRequest.getStartDateTime(), appointmentRequest.getEndDateTime(),
                        appointmentRequest.getEndDateTime())) {
            throw new AlreadyExistsException("conflict: doctor has the reserved time slot during the provided time period");
        }
        PatientRecord patientRecord = patientRecordRepository.findByPatientRecordId(appointmentRequest.getPatientRecordId());
        if (patientRecord.getStatus() != PatientRecordStatus.DOCTOR_IN_PROGRESS) {
            throw new ResourceNotFoundException(String.format("patient record %s not found", appointmentRequest.getPatientRecordId()));
        }
        // save doctor appointment
        DoctorAppointment doctorAppointment = appointmentMapper.fromAppointmentRequestToDoctorAppointment(appointmentRequest);
        doctorAppointment = appointmentRepository.save(doctorAppointment);

        // update patient record
        patientRecordService.afterAppointment(doctorAppointment, patientRecord, PatientRecordStatus.DOCTOR_APPOINTMENT);
    }

    public Page<AppointmentResponse> getDoctorAppointments(Pageable pageable) {
        User user = userCommonService.getUser();

        Page<DoctorAppointment> pages = appointmentRepository.findByDoctorIdAndStartDateTimeGreaterThanEqualOrderByCreatedAtDesc(user.getUserId(), TimeUtil.nowUTC(), pageable);

        return pages.map(appointmentMapper::toAppointmentResponse);
    }

    public List<AppointmentListForDateResponse> getDoctorAppointmentsByDate(AppointmentListForDateRequest request) {
        if (request.getDate() == null) {
            throw new InvalidUserRequestException("date cannot be null");
        }
        User user = userCommonService.getUser();

        return appointmentRepository.findByDoctorIdAndStartDateTimeBetweenOrderByCreatedAtDesc(user.getUserId(), request.getDate(), request.getDate().plusDays(1));
    }

    public Page<AssignedPatientResponse> getAssignedPatients(Pageable pageable) {
        User user = userCommonService.getUser();

        Page<AssignedPatient> assignedPatientPage = assignedPatientRepository.findByDoctorRegistrationNumberOrderByCreatedAtDesc(user.getRegistrationNumber(), pageable);

        return assignedPatientPage.map(assignedPatientMapper::toAssignedPatientResponse);
    }

    public PatientRecordResponse getActivePatient(String patientRecordId) {
        PatientRecord patientRecord = patientRecordRepository.findByPatientRecordId(patientRecordId);
        PatientRecordResponse response = new PatientRecordResponse();
        response.setPatient(userMapper.toUserResponse(userRepository.findByUserIdAndDeletedFalse(patientRecord.getPatientId())));
        response.setRecordId(patientRecordId);
        response.setCreatedAt(patientRecord.getCreatedAt());
        response.setAssessmentResult(patientService.getAssessmentResult(patientRecord.getAssessmentResultId()));
        return response;
    }

    public void rejectPatient(String patientRecordId) {
        PatientRecord patientRecord = patientRecordRepository.findByPatientRecordId(patientRecordId);
        if (patientRecord != null &&
                (patientRecord.getStatus() == PatientRecordStatus.DOCTOR_IN_PROGRESS || patientRecord.getStatus() == PatientRecordStatus.DOCTOR_APPOINTMENT)) {
            assignedPatientRepository.deleteById(patientRecord.getAssignedPatientId());

            if (patientRecord.getAppointmentId() != null &&
                    patientRecord.getStatus() == PatientRecordStatus.DOCTOR_APPOINTMENT) {
                // delete doctor appointment
                appointmentRepository.deleteByAppointmentId(patientRecord.getAppointmentId());
            }

            patientRecordService.afterRejectingPatient(patientRecord, PatientRecordStatus.DOCTOR_REJECTED);
            return;
        }
        throw new ResourceNotFoundException("patient record not found");
    }
}