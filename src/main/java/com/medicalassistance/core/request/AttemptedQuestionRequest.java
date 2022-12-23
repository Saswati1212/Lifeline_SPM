package com.medicalassistance.core.request;

public class AttemptedQuestionRequest {
    String questionId;
    int answer;

    public AttemptedQuestionRequest() {
    }

    public AttemptedQuestionRequest(String questionId, int answer) {
        this.questionId = questionId;
        this.answer = answer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
