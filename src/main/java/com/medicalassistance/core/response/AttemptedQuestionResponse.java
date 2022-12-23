package com.medicalassistance.core.response;

public class AttemptedQuestionResponse {
    String question;
    Integer answer;

    public AttemptedQuestionResponse(String question, Integer answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(Integer answer) {
        this.answer = answer;
    }
}
