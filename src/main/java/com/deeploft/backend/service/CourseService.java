package com.deeploft.backend.service;

import com.deeploft.backend.model.Course;
import com.deeploft.backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses(String query, Boolean freeOnly) {
        List<Course> courses;
        if (query != null && !query.isEmpty()) {
            courses = courseRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(query, query);
        } else {
            courses = courseRepository.findAll();
        }

        if (freeOnly != null) {
            if (freeOnly) {
                return courses.stream().filter(c -> c.getPrice() == 0).collect(Collectors.toList());
            } else {
                return courses.stream().filter(c -> c.getPrice() > 0).collect(Collectors.toList());
            }
        }
        return courses;
    }

    public List<Course> getAiSearchRecommendations(String intent) {
        String lowerIntent = intent.toLowerCase();
        List<Course> all = courseRepository.findAll();

        // Simulated AI Semantic Logic: Match intent to keywords in title, description, and category
        return all.stream().filter(c -> {
            boolean matches = false;
            // Topic: Robotics/Engineering
            if (lowerIntent.contains("robot") || lowerIntent.contains("build") || lowerIntent.contains("hardware")) {
                matches = c.getCategory().equalsIgnoreCase("Science") || c.getTitle().toLowerCase().contains("android");
            }
            // Topic: Data/AI
            if (lowerIntent.contains("intelligent") || lowerIntent.contains("data") || lowerIntent.contains("smart")) {
                matches = matches || c.getCategory().equalsIgnoreCase("Artificial Intelligence") || c.getTitle().toLowerCase().contains("ai");
            }
            // Topic: Coding/Apps
            if (lowerIntent.contains("code") || lowerIntent.contains("app") || lowerIntent.contains("mobile")) {
                matches = matches || c.getCategory().equalsIgnoreCase("Development") || c.getTitle().toLowerCase().contains("web");
            }
            // Generic match
            return matches || c.getTitle().toLowerCase().contains(lowerIntent) || c.getDescription().toLowerCase().contains(lowerIntent);
        }).limit(5).collect(Collectors.toList());
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course updatedCourse) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setTitle(updatedCourse.getTitle());
                    course.setDescription(updatedCourse.getDescription());
                    course.setPrice(updatedCourse.getPrice());
                    course.setImageUrl(updatedCourse.getImageUrl());
                    course.setCategory(updatedCourse.getCategory());
                    // Keep existing lessons if not provided in update
                    if (updatedCourse.getLessons() != null) {
                        course.setLessons(updatedCourse.getLessons());
                    }
                    return courseRepository.save(course);
                }).orElse(null);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}