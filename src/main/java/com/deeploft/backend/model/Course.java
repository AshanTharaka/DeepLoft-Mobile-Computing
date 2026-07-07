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
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String instructor;
    private String instructorEmail;
    private double price;
    private String description;
    private String imageUrl;
    private String category;
    private String status; // "DRAFT", "PENDING_APPROVAL", "PUBLISHED", "REJECTED"
    private String adminReviewComment;
    @Column(length = 2000)
    private String courseAiSummary;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "course_id")
    private List<Lesson> lessons;
}