package com.deeploft.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherStatsDTO {
    private String name;
    private String email;
    private int totalStudents;
    private double averageRating;
    private int totalCourses;
    private String aiBio;
}