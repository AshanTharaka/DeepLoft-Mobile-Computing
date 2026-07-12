package com.example.deeploft.models;

import java.io.Serializable;

public class LessonProgress implements Serializable {
    private Long id;
    private String userEmail;
    private Long courseId;
    private Long lessonId;
    private boolean completed;

    public LessonProgress(String userEmail, Long courseId, Long lessonId, boolean completed) {
        this.userEmail = userEmail;
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.completed = completed;
    }

    public Long getLessonId() { return lessonId; }
    public boolean isCompleted() { return completed; }
}