package com.example.deeploft.models;

import java.io.Serializable;
import java.util.List;

public class StudyPlan implements Serializable {
    private String userEmail;
    private String courseTitle;
    private String goal;
    private List<String> dailyTasks;

    public StudyPlan(String userEmail, String courseTitle, String goal) {
        this.userEmail = userEmail;
        this.courseTitle = courseTitle;
        this.goal = goal;
    }

    public String getCourseTitle() { return courseTitle; }
    public String getGoal() { return goal; }
    public List<String> getDailyTasks() { return dailyTasks; }
}