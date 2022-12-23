package com.medicalassistance.core.response;

import com.medicalassistance.core.entity.BooleanQuestion;

public class BooleanQuestionProjection {
    String questionId;
    String question;

    public BooleanQuestionProjection(BooleanQuestion booleanQuestion) {
        this.questionId = booleanQuestion.getQuestionId();
        this.question = booleanQuestion.getQuestion();
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
