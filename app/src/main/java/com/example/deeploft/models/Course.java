package com.example.deeploft.models;

import java.io.Serializable;
import java.util.List;

public class Course implements Serializable {
    private Long id;
    private String title;
    private String instructor;
    private String instructorEmail;
    private double price;
    private String imageUrl;
    private String description;
    private String category;
    private String status; // "DRAFT", "PENDING_APPROVAL", "PUBLISHED", "REJECTED"
    private String adminReviewComment;
    private String courseAiSummary;
    private List<Lesson> lessons;

    public Course(Long id, String title, String instructor, double price, String imageUrl) {
        this.id = id;
        this.title = title;
        this.instructor = instructor;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getInstructor() { return instructor; }
    public String getInstructorEmail() { return instructorEmail; }
    public void setInstructorEmail(String instructorEmail) { this.instructorEmail = instructorEmail; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAdminReviewComment() { return adminReviewComment; }
    public void setAdminReviewComment(String adminReviewComment) { this.adminReviewComment = adminReviewComment; }
    public String getCourseAiSummary() { return courseAiSummary; }
    public void setCourseAiSummary(String courseAiSummary) { this.courseAiSummary = courseAiSummary; }
    public List<Lesson> getLessons() { return lessons; }
    public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }
}