package com.medicalassistance.core.response;

import java.util.List;

public class AssessmentResultResponse {
    private List<AttemptedQuestionResponse> attemptedQuestions;

    public List<AttemptedQuestionResponse> getAttemptedQuestions() {
        return attemptedQuestions;
    }

    public void setAttemptedQuestions(List<AttemptedQuestionResponse> attemptedQuestions) {
        this.attemptedQuestions = attemptedQuestions;
    }

    public boolean addAttemptedQuestion(AttemptedQuestionResponse attemptedQuestion) {
        return this.attemptedQuestions.add(attemptedQuestion);
    }
}
