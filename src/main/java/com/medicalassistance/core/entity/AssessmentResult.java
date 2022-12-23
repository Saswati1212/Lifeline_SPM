package com.medicalassistance.core.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("assessment_results")
public class AssessmentResult extends DateDomainObject {
    @Id
    private String assessmentResultId;

    private String assessmentId;

    private String patientId;

    private List<AttemptedQuestion> attemptedQuestions;

    public String getAssessmentResultId() {
        return assessmentResultId;
    }

    public void setAssessmentResultId(String assessmentResultId) {
        this.assessmentResultId = assessmentResultId;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public List<AttemptedQuestion> getAttemptedQuestions() {
        return attemptedQuestions;
    }

    public void setAttemptedQuestions(List<AttemptedQuestion> attemptedQuestions) {
        this.attemptedQuestions = attemptedQuestions;
    }
}
