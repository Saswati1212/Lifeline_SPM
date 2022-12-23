package com.medicalassistance.core.response;

import java.util.ArrayList;
import java.util.List;

public class AssessmentResponse {
    List<BooleanQuestionProjection> questions = new ArrayList<>();

    public List<BooleanQuestionProjection> getQuestions() {
        return questions;
    }

    public boolean addQuestion(BooleanQuestionProjection question) {
        return this.questions.add(question);
    }
}
