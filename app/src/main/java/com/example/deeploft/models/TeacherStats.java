package com.example.deeploft.models;

import java.io.Serializable;

public class TeacherStats implements Serializable {
    private String name;
    private int totalStudents;
    private double averageRating;
    private int totalCourses;
    private String aiBio;

    public String getName() { return name; }
    public int getTotalStudents() { return totalStudents; }
    public double getAverageRating() { return averageRating; }
    public int getTotalCourses() { return totalCourses; }
    public String getAiBio() { return aiBio; }
}