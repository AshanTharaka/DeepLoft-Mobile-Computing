package com.deeploft.backend.service;

import com.deeploft.backend.model.Course;
import com.deeploft.backend.model.Enrollment;
import com.deeploft.backend.repository.CourseRepository;
import com.deeploft.backend.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<Course> getRecommendations(String email) {
        // AI Logic: Find categories the user is already enrolled in
        List<Enrollment> enrollments = enrollmentRepository.findByUserEmail(email);
        
        if (enrollments.isEmpty()) {
            // New user: Return latest PUBLISHED courses
            return courseRepository.findAll().stream()
                    .filter(c -> "PUBLISHED".equalsIgnoreCase(c.getStatus()))
                    .limit(5)
                    .collect(Collectors.toList());
        }

        List<String> interests = enrollments.stream()
                .map(e -> e.getCourse().getCategory())
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // Find courses in those categories that the user hasn't enrolled in yet AND are PUBLISHED
        List<Long> enrolledCourseIds = enrollments.stream()
                .map(e -> e.getCourse().getId())
                .collect(Collectors.toList());

        return courseRepository.findAll().stream()
                .filter(c -> "PUBLISHED".equalsIgnoreCase(c.getStatus()))
                .filter(c -> interests.contains(c.getCategory()))
                .filter(c -> !enrolledCourseIds.contains(c.getId()))
                .limit(5)
                .collect(Collectors.toList());
    }
}