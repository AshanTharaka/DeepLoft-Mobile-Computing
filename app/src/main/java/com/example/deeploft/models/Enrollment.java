package com.example.deeploft.models;

import java.io.Serializable;

public class Enrollment implements Serializable {
    private Long id;
    private Course course;
    private String userEmail;
    private String enrollmentDate;
    private double amountPaid;
    private Long lastWatchedLessonId;

    public Enrollment(Course course, String userEmail, double amountPaid) {
        this.course = course;
        this.userEmail = userEmail;
        this.amountPaid = amountPaid;
    }

    public Course getCourse() { return course; }
    public String getUserEmail() { return userEmail; }
    public Long getLastWatchedLessonId() { return lastWatchedLessonId; }
    public void setLastWatchedLessonId(Long lastWatchedLessonId) { this.lastWatchedLessonId = lastWatchedLessonId; }
}