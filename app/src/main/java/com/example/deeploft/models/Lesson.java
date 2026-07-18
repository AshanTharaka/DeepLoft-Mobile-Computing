package com.example.deeploft.models;

import java.io.Serializable;

public class Lesson implements Serializable {
    private Long id;
    private String title;
    private String videoUrl;
    private int durationMinutes;
    private String aiSummary;

    public Lesson() {}

    public Lesson(String title, String videoUrl) {
        this.title = title;
        this.videoUrl = videoUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
}