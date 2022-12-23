package com.medicalassistance.core.service;

import com.medicalassistance.core.common.AuthorityName;
import com.medicalassistance.core.common.PatientRecordStatus;
import com.medicalassistance.core.common.UserCommonService;
import com.medicalassistance.core.entity.AssignedPatient;
import com.medicalassistance.core.entity.CounselorAppointment;
import com.medicalassistance.core.entity.PatientRecord;
import com.medicalassistance.core.entity.User;
import com.medicalassistance.core.exception.AlreadyExistsException;
import com.medicalassistance.core.exception.InvalidUserRequestException;
import com.medicalassistance.core.exception.ResourceNotFoundException;
import com.medicalassistance.core.mapper.AppointmentMapper;
import com.medicalassistance.core.mapper.UserMapper;
import com.medicalassistance.core.repository.*;
import com.medicalassistance.core.request.AppointmentListForDateRequest;
import com.medicalassistance.core.request.AppointmentRequest;
import com.medicalassistance.core.request.DoctorAssignmentRequest;
import com.medicalassistance.core.response.*;
import com.medicalassistance.core.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class CounselorService {
    @Autowired
    UserCommonService userCommonService;

    @Autowired
    CounselorAppointmentRepository appointmentRepository;

    @Autowired
    AppointmentMapper appointmentMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivePatientRepository activePatientRepository;

    @Autowired
    PatientRecordService patientRecordService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AssignedPatientRepository assignedPatientRepository;

    @Autowired
    PatientService patientService;

    @Autowired
    PatientRecordRepository patientRecordRepository;

    public void storeCounselorAppointment(AppointmentRequest appointmentRequest) {
        String counselorId = userCommonService.getUser().getUserId();
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
        if (appointmentRepository.existsByCounselorIdAndStartDateTimeBetweenOrStartDateTimeEquals(
                counselorId,
                appointmentRequest.getStartDateTime(), appointmentRequest.getEndDateTime(),
                appointmentRequest.getStartDateTime()) ||
                appointmentRepository.existsByCounselorIdAndEndDateTimeBetweenOrEndDateTimeEquals(
                        counselorId,
                        appointmentRequest.getStartDateTime(), appointmentRequest.getEndDateTime(),
                        appointmentRequest.getEndDateTime())) {
            throw new AlreadyExistsException("conflict: counselor has the reserved time slot during the provided time period");
        }
        PatientRecord patientRecord = patientRecordRepository.findByPatientRecordId(appointmentRequest.getPatientRecordId());
        if (patientRecord.getStatus() != PatientRecordStatus.COUNSELOR_IN_PROGRESS) {
            throw new ResourceNotFoundException(String.format("patient record %s not found", appointmentRequest.getPatientRecordId()));
        }
        // save counselor appointment
        CounselorAppointment counselorAppointment = appointmentMapper.fromAppointmentRequestToCounselorAppointment(appointmentRequest);
        counselorAppointment = appointmentRepository.save(counselorAppointment);

        // update patient record
        patientRecordService.afterAppointment(counselorAppointment, patientRecord, PatientRecordStatus.COUNSELOR_APPOINTMENT);
    }

    public Page<AppointmentResponse> getCounselorAppointments(Pageable pageable) {
        User user = userCommonService.getUser();

        Page<CounselorAppointment> pages = appointmentRepository.findByCounselorIdAndStartDateTimeGreaterThanEqualOrderByCreatedAtDesc(user.getUserId(), TimeUtil.nowUTC(), pageable);

        return pages.map(appointmentMapper::toAppointmentResponse);
    }

    public List<AppointmentListForDateResponse> getCounselorAppointmentsByDate(AppointmentListForDateRequest request) {
        if (request.getDate() == null) {
            throw new InvalidUserRequestException("date cannot be null");
        }
        User user = userCommonService.getUser();

        return appointmentRepository.findByCounselorIdAndStartDateTimeBetweenOrderByCreatedAtDesc(user.getUserId(), request.getDate(), request.getDate().plusDays(1));
    }

    public Page<CounselorDoctorCardResponse> getDoctorPage(Pageable pageable) {
        Page<User> page = userRepository.findByAuthoritiesContainsAndDeletedFalseOrderByCreatedAtDesc(AuthorityName.ROLE_DOCTOR, pageable);

        return page.map(userMapper::toCounselorDoctorCardResponse);
    }

    public PatientRecordResponse getActivePatient(String patientRecordId) {
        PatientRecord patientRecord = patientRecordRepository.findByPatientRecordId(patientRecordId);
        PatientRecordResponse response = new PatientRecordResponse();
        response.setPatient(userMapper.toUserResponse(userRepository.findByUserIdAndDeletedFalse(patientRecord.getPatientId())));
        response.setRecordId(patientRecord.getPatientRecordId());
        response.setCreatedAt(patientRecord.getCreatedAt());
        response.setAssessmentResult(patientService.getAssessmentResult(patientRecord.getAssessmentResultId()));
        return response;
    }

    public void assignDoctorToPatient(DoctorAssignmentRequest doctorAssignmentRequest) {
        String counselorRegistrationNumber = userCommonService.getUser().getRegistrationNumber();
        if (!patientRecordRepository.existsByPatientRecordId(doctorAssignmentRequest.getActivePatientId())) {
            throw new ResourceNotFoundException(String.format("patient record %s not found", doctorAssignmentRequest.getActivePatientId()));
        }
        if (!userRepository.existsByRegistrationNumberAndDeletedFalse(doctorAssignmentRequest.getDoctorRegistrationNumber())) {
            throw new ResourceNotFoundException(String.format("doctor with %s not found", doctorAssignmentRequest.getDoctorRegistrationNumber()));
        }
        // check if the patient record has already been assigned to a doctor
        if (assignedPatientRepository.existsByPatientRecordId(doctorAssignmentRequest.getActivePatientId())) {
            throw new AlreadyExistsException(String.format("patient record %s is already assigned to a doctor", doctorAssignmentRequest.getActivePatientId()));
        }

        PatientRecord patientRecord = patientRecordRepository.findByPatientRecordId(doctorAssignmentRequest.getActivePatientId());
        if (patientRecord.getStatus() != PatientRecordStatus.COUNSELOR_IN_PROGRESS &&
                patientRecord.getStatus() != PatientRecordStatus.COUNSELOR_APPOINTMENT) {
            throw new ResourceNotFoundException(String.format("patient record %s not found", doctorAssignmentRequest.getActivePatientId()));
        }

        // before forwarding to a doctor, delete any existing appointment with the counselor.
        if (patientRecord.getStatus() == PatientRecordStatus.COUNSELOR_APPOINTMENT &&
                patientRecord.getAppointmentId() != null) {
            appointmentRepository.deleteByAppointmentId(patientRecord.getAppointmentId());
        }

        // save assigned patient record
        AssignedPatient assignedPatient = new AssignedPatient();
        assignedPatient.setPatientRecordId(patientRecord.getPatientRecordId());
        assignedPatient.setDoctorRegistrationNumber(doctorAssignmentRequest.getDoctorRegistrationNumber());
        assignedPatient.setCounselorRegistrationNumber(counselorRegistrationNumber);
        assignedPatient.setPatientId(patientRecord.getPatientId());
        assignedPatient = assignedPatientRepository.save(assignedPatient);

        // update patient record after assigning a doctor to active patient record
        patientRecordService.afterAssigningDoctor(assignedPatient, patientRecord);
    }

    public void rejectPatient(String patientRecordId) {
        PatientRecord patientRecord = patientRecordRepository.findByPatientRecordId(patientRecordId);
        if (patientRecord != null &&
                (patientRecord.getStatus() == PatientRecordStatus.COUNSELOR_IN_PROGRESS || patientRecord.getStatus() == PatientRecordStatus.COUNSELOR_APPOINTMENT)) {
            activePatientRepository.deleteByActivePatientId(patientRecord.getActivePatientId());

            if (patientRecord.getAppointmentId() != null &&
                    patientRecord.getStatus() == PatientRecordStatus.COUNSELOR_APPOINTMENT) {
                // delete counselor appointment
                appointmentRepository.deleteByAppointmentId(patientRecord.getAppointmentId());
            }

            patientRecordService.afterRejectingPatient(patientRecord, PatientRecordStatus.COUNSELOR_REJECTED);
            return;
        }
        throw new ResourceNotFoundException("patient record not found");
    }
}