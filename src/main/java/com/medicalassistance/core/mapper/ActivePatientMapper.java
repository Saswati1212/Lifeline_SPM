package com.medicalassistance.core.mapper;


import com.medicalassistance.core.entity.ActivePatient;
import com.medicalassistance.core.repository.UserRepository;
import com.medicalassistance.core.response.PatientRecordCardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivePatientMapper {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public PatientRecordCardResponse toPatientRecordCardResponse(ActivePatient activePatient) {
        PatientRecordCardResponse cardResponse = new PatientRecordCardResponse();
        cardResponse.setPatientRecordId(activePatient.getPatientRecordId());
        cardResponse.setPatient(userMapper.toUserCardResponse(userRepository.findByUserIdAndDeletedFalse(activePatient.getPatientId())));
        cardResponse.setAssessmentCreatedAt(activePatient.getCreatedAt());
        return cardResponse;
    }
}