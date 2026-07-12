package com.example.deeploft.models;

import java.io.Serializable;

public class Certificate implements Serializable {
    private Long id;
    private String userEmail;
    private String userName;
    private String courseTitle;
    private String issuedDate;
    private Long courseId;

    public Certificate() {}

    public Certificate(String userEmail, String userName, String courseTitle, Long courseId) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.courseTitle = courseTitle;
        this.courseId = courseId;
    }

    public String getCourseTitle() { return courseTitle; }
    public String getIssuedDate() { return issuedDate; }
}