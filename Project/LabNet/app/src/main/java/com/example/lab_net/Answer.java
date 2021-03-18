package com.example.lab_net;

public class Answer {
    private String questionID;
    private String answerID;
    private String answerText;

    public Answer(String questionId, String answerId, String answerText) {
        this.questionID = questionId;
        this.answerID = answerId;
        this.answerText = answerText;
    }


    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getAnswerID() {
        return answerID;
    }

    public void setAnswerID(String answerID) {
        this.answerID = answerID;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
