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
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String userEmail;
    private String userName;
    private String courseTitle;
    private LocalDateTime issuedDate;
    
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}