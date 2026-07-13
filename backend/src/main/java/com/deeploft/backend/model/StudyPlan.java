package com.deeploft.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String userEmail;
    private String courseTitle;
    private String goal; // e.g., "Complete in 2 weeks"
    
    @ElementCollection
    private List<String> dailyTasks;
}