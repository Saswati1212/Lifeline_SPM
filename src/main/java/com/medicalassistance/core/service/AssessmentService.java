package com.medicalassistance.core.service;

import com.medicalassistance.core.common.UserCommonService;
import com.medicalassistance.core.entity.Assessment;
import com.medicalassistance.core.entity.AssessmentResult;
import com.medicalassistance.core.entity.AttemptedQuestion;
import com.medicalassistance.core.exception.AlreadyExistsException;
import com.medicalassistance.core.repository.*;
import com.medicalassistance.core.request.AssessmentResultRequest;
import com.medicalassistance.core.request.AttemptedQuestionRequest;
import com.medicalassistance.core.response.AssessmentResponse;
import com.medicalassistance.core.response.BooleanQuestionProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssessmentService {
    @Autowired
    AssessmentRepository assessmentRepository;

    @Autowired
    AssessmentResultRepository assessmentResultRepository;

    @Autowired
    ActivePatientRepository activePatientRepository;

    @Autowired
    UserCommonService userCommonService;

    @Autowired
    BooleanQuestionRepository booleanQuestionRepository;

    @Autowired
    PatientRecordRepository patientRecordRepository;

    @Autowired
    PatientRecordService patientRecordService;

    @Autowired
    AssignedPatientRepository assignedPatientRepository;

    public AssessmentResponse getAssessment(String assessmentId) {
        AssessmentResponse response = new AssessmentResponse();
        Assessment assessment = assessmentRepository.findByAssessmentId(assessmentId);
        for (String questionId : assessment.getQuestionIds()) {
            response.addQuestion(new BooleanQuestionProjection(booleanQuestionRepository.findByQuestionId(questionId)));
        }
        return response;
    }

    public void storeAssessmentResult(String assessmentId, AssessmentResultRequest assessmentRequest) {
        String userId = userCommonService.getUser().getUserId();

        if (activePatientRepository.existsByPatientId(userId) ||
                assignedPatientRepository.existsByPatientId(userId)) {
            throw new AlreadyExistsException("You already have an active patient file with us!");
        }

        AssessmentResult assessmentResult = new AssessmentResult();

        // store the assessment result
        assessmentResult.setAssessmentId(assessmentId);
        List<AttemptedQuestion> attemptedQuestions = new ArrayList<>();
        for (int i = 0; i < assessmentRequest.getQuestions().size(); i++) {
            AttemptedQuestionRequest questionRequest = assessmentRequest.getQuestions().get(i);
            attemptedQuestions.add(new AttemptedQuestion(questionRequest.getQuestionId(), questionRequest.getAnswer()));
        }
        assessmentResult.setPatientId(userId);
        assessmentResult.setAttemptedQuestions(attemptedQuestions);
        assessmentResult = assessmentResultRepository.save(assessmentResult);

        // create patient record and active patient record.
        patientRecordService.afterAssessment(assessmentResult);
    }
}
