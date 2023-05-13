package com.example.application.data.service;

public class Subtitle {
    private String text;
    private long startTime, endTime;

    public Subtitle(String text, long startTime, long endTime) {
        this.text = text;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
