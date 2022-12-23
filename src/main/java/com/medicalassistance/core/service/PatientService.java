package com.medicalassistance.core.service;

import com.medicalassistance.core.common.AuthorityName;
import com.medicalassistance.core.common.PatientRecordStatus;
import com.medicalassistance.core.common.UserCommonService;
import com.medicalassistance.core.entity.*;
import com.medicalassistance.core.mapper.ActivePatientMapper;
import com.medicalassistance.core.mapper.UserMapper;
import com.medicalassistance.core.repository.*;
import com.medicalassistance.core.response.AssessmentResultResponse;
import com.medicalassistance.core.response.AttemptedQuestionResponse;
import com.medicalassistance.core.response.PatientRecordCardResponse;
import com.medicalassistance.core.response.PatientRecordStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {
    @Autowired
    ActivePatientRepository activePatientRepository;

    @Autowired
    AssessmentResultRepository assessmentResultRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserCommonService userCommonService;

    @Autowired
    BooleanQuestionRepository booleanQuestionRepository;

    @Autowired
    PatientRecordRepository patientRecordRepository;

    @Autowired
    CounselorAppointmentRepository counselorAppointmentRepository;

    @Autowired
    DoctorAppointmentRepository doctorAppointmentRepository;

    @Autowired
    ActivePatientMapper activePatientMapper;

    public Page<PatientRecordCardResponse> getActivePatients(Pageable pageable) {
        Page<ActivePatient> activePatients = activePatientRepository.findAll(pageable);

        return activePatients.map(activePatientMapper::toPatientRecordCardResponse);
    }

    public AssessmentResultResponse getAssessmentResult(String assessmentResultId) {
        AssessmentResultResponse assessmentResultResponse = new AssessmentResultResponse();
        AssessmentResult assessmentResult = assessmentResultRepository.findByAssessmentResultId(assessmentResultId);
        List<AttemptedQuestionResponse> attemptedQuestionResponses = new ArrayList<>();
        for (AttemptedQuestion attemptedQuestion : assessmentResult.getAttemptedQuestions()) {
            attemptedQuestionResponses.add(new AttemptedQuestionResponse(
                    booleanQuestionRepository.findByQuestionId(attemptedQuestion.getQuestionId()).getQuestion(),
                    attemptedQuestion.getAnswer()
            ));
        }
        assessmentResultResponse.setAttemptedQuestions(attemptedQuestionResponses);
        return assessmentResultResponse;
    }

    public PatientRecordStatusResponse getPatientRecordStatus() {
        User user = userCommonService.getUser();
        return getPatientRecordStatus(user);
    }

    public PatientRecordStatusResponse getPatientRecordStatus(User user) {
        PatientRecordStatusResponse patientRecordStatusResponse = new PatientRecordStatusResponse();
        patientRecordStatusResponse.setPatientRecordStatus(PatientRecordStatus.NULL);
        if (!user.getAuthorities().contains(AuthorityName.ROLE_PATIENT)) {
            return patientRecordStatusResponse;
        }
        String patientId = user.getUserId();
        PatientRecord patientRecord = patientRecordRepository.findTop1ByPatientIdOrderByCreatedAtDesc(patientId);
        if (patientRecord == null) {
            return patientRecordStatusResponse;
        }
        patientRecordStatusResponse.setPatientRecordStatus(patientRecord.getStatus());
        patientRecordStatusResponse.setCreatedAt(patientRecord.getCreatedAt());
        patientRecordStatusResponse.setUpdatedAt(patientRecord.getUpdatedAt());

        if (patientRecord.getStatus() == PatientRecordStatus.COUNSELOR_APPOINTMENT) {
            CounselorAppointment appointment = counselorAppointmentRepository.findByAppointmentId(patientRecord.getAppointmentId());
            patientRecordStatusResponse.setStartDateTime(appointment.getStartDateTime());
            patientRecordStatusResponse.setEndDateTime(appointment.getEndDateTime());
        }
        if (patientRecord.getStatus() == PatientRecordStatus.DOCTOR_APPOINTMENT) {
            DoctorAppointment appointment = doctorAppointmentRepository.findByAppointmentId(patientRecord.getAppointmentId());
            patientRecordStatusResponse.setStartDateTime(appointment.getStartDateTime());
            patientRecordStatusResponse.setEndDateTime(appointment.getEndDateTime());
        }
        return patientRecordStatusResponse;
    }
}
