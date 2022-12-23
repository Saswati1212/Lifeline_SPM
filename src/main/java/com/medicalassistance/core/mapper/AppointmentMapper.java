package com.medicalassistance.core.mapper;


import com.medicalassistance.core.common.UserCommonService;
import com.medicalassistance.core.entity.*;
import com.medicalassistance.core.exception.ResourceNotFoundException;
import com.medicalassistance.core.repository.ActivePatientRepository;
import com.medicalassistance.core.repository.PatientRecordRepository;
import com.medicalassistance.core.repository.UserRepository;
import com.medicalassistance.core.request.AppointmentRequest;
import com.medicalassistance.core.response.AppointmentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivePatientRepository activePatientRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCommonService userCommonService;

    @Autowired
    PatientRecordRepository patientRecordRepository;

    public AppointmentResponse toAppointmentResponse(Appointment appointment) {
        PatientRecord patientRecord = patientRecordRepository.findByPatientRecordId(appointment.getPatientRecordId());
        if (patientRecord != null) {
            AppointmentResponse appointmentResponse = new AppointmentResponse();
            appointmentResponse.setPatientRecordId(appointment.getPatientRecordId());
            appointmentResponse.setCreatedAt(appointment.getCreatedAt());
            appointmentResponse.setStartDateTime(appointment.getStartDateTime());
            appointmentResponse.setEndDateTime(appointment.getEndDateTime());
            appointmentResponse.setPatient(userMapper.toUserResponse(userRepository.findByUserIdAndDeletedFalse(patientRecord.getPatientId())));
            return appointmentResponse;
        }
        throw new ResourceNotFoundException("active patient record doesn't found");
    }

    public DoctorAppointment fromAppointmentRequestToDoctorAppointment(AppointmentRequest appointmentRequest) {
        User user = userCommonService.getUser();
        DoctorAppointment doctorAppointment = new DoctorAppointment();
        doctorAppointment.setPatientId(user.getUserId());
        doctorAppointment.setStartDateTime(appointmentRequest.getStartDateTime());
        doctorAppointment.setEndDateTime(appointmentRequest.getEndDateTime());
        doctorAppointment.setDoctorId(user.getUserId());
        doctorAppointment.setPatientRecordId(appointmentRequest.getPatientRecordId());
        return doctorAppointment;
    }

    public CounselorAppointment fromAppointmentRequestToCounselorAppointment(AppointmentRequest appointmentRequest) {
        User user = userCommonService.getUser();
        CounselorAppointment counselorAppointment = new CounselorAppointment();
        counselorAppointment.setPatientId(user.getUserId());
        counselorAppointment.setStartDateTime(appointmentRequest.getStartDateTime());
        counselorAppointment.setEndDateTime(appointmentRequest.getEndDateTime());
        counselorAppointment.setCounselorId(user.getUserId());
        counselorAppointment.setPatientRecordId(appointmentRequest.getPatientRecordId());
        return counselorAppointment;
    }
}
