package com.codewithshubh.servozone.Model;

public class FaqModel {
    private String question, answer;
    private boolean isOpen;


    public FaqModel() {
    }

    public FaqModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
