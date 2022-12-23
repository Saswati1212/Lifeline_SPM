package com.medicalassistance.core.request;

import java.util.List;

public class AssessmentResultRequest {
    List<AttemptedQuestionRequest> questions;

    public List<AttemptedQuestionRequest> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AttemptedQuestionRequest> questions) {
        this.questions = questions;
    }
}
