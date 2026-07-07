package com.deeploft.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProgressDTO {
    private String studentEmail;
    private String courseTitle;
    private int completionPercentage;
}