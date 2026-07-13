package com.deeploft.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String userEmail;
    private String userName;
    private String comment;
    private int rating; // 1 to 5
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}