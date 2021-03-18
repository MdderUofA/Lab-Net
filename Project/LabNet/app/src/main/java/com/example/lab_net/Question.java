package com.example.lab_net;

public class Question {

    private String questionId;
    private String questionText;

    public Question(String questionId, String questionText) {
        this.questionId = questionId;
        this.questionText = questionText;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }
}
