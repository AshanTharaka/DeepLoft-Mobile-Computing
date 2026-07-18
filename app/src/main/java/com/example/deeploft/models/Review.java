package com.example.deeploft.models;

import java.io.Serializable;

public class Review implements Serializable {
    private Long id;
    private String userEmail;
    private String userName;
    private String comment;
    private int rating;
    private String date;
    private Long courseId;

    public Review(String userEmail, String userName, String comment, int rating, Long courseId) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.comment = comment;
        this.rating = rating;
        this.courseId = courseId;
    }

    public String getUserName() { return userName; }
    public String getComment() { return comment; }
    public int getRating() { return rating; }
}