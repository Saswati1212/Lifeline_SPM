package com.medicalassistance.core.entity;


public class AttemptedQuestion {
    private String questionId;

    private Integer answer;

    public AttemptedQuestion(String questionId, Integer answer) {
        this.questionId = questionId;
        this.answer = answer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(Integer answer) {
        this.answer = answer;
    }
}