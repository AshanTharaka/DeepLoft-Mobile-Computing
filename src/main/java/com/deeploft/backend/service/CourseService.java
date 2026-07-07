package com.deeploft.backend.service;

import com.deeploft.backend.model.ActivityLog;
import com.deeploft.backend.model.Course;
import com.deeploft.backend.repository.ActivityLogRepository;
import com.deeploft.backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private void logActivity(String instructor, String message) {
        ActivityLog log = new ActivityLog();
        log.setInstructorName(instructor);
        log.setMessage(message);
        log.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(log);
    }

    public List<Course> getAllCourses(String query, String category, Boolean freeOnly, String status) {
        List<Course> courses = courseRepository.findAll();

        if (query != null && !query.isEmpty()) {
            courses = courses.stream()
                    .filter(c -> c.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                                 (c.getDescription() != null && c.getDescription().toLowerCase().contains(query.toLowerCase())))
                    .collect(Collectors.toList());
        }

        if (category != null && !category.isEmpty()) {
            courses = courses.stream()
                    .filter(c -> c.getCategory() != null && c.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isEmpty()) {
            courses = courses.stream().filter(c -> status.equalsIgnoreCase(c.getStatus())).collect(Collectors.toList());
        }

        if (freeOnly != null) {
            if (freeOnly) {
                courses = courses.stream().filter(c -> c.getPrice() == 0).collect(Collectors.toList());
            } else {
                courses = courses.stream().filter(c -> c.getPrice() > 0).collect(Collectors.toList());
            }
        }
        return courses;
    }

    public List<Course> getAiSearchRecommendations(String intent) {
        String lowerIntent = intent.toLowerCase();
        List<Course> all = courseRepository.findAll();

        return all.stream().filter(c -> {
            boolean matches = false;
            if (lowerIntent.contains("robot") || lowerIntent.contains("build") || lowerIntent.contains("hardware")) {
                matches = c.getCategory().equalsIgnoreCase("Science") || c.getTitle().toLowerCase().contains("android");
            }
            if (lowerIntent.contains("intelligent") || lowerIntent.contains("data") || lowerIntent.contains("smart")) {
                matches = matches || c.getCategory().equalsIgnoreCase("Artificial Intelligence") || c.getTitle().toLowerCase().contains("ai");
            }
            if (lowerIntent.contains("code") || lowerIntent.contains("app") || lowerIntent.contains("mobile")) {
                matches = matches || c.getCategory().equalsIgnoreCase("Development") || c.getTitle().toLowerCase().contains("web");
            }
            return matches || c.getTitle().toLowerCase().contains(lowerIntent) || (c.getDescription() != null && c.getDescription().toLowerCase().contains(lowerIntent));
        }).limit(5).collect(Collectors.toList());
    }

    public Course createCourse(Course course) {
        generateAiCourseSummary(course);
        Course saved = courseRepository.save(course);
        logActivity(course.getInstructor(), "Published a new course: " + course.getTitle());
        return saved;
    }

    public Course updateCourse(Long id, Course updatedCourse) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setTitle(updatedCourse.getTitle());
                    course.setDescription(updatedCourse.getDescription());
                    course.setPrice(updatedCourse.getPrice());
                    course.setImageUrl(updatedCourse.getImageUrl());
                    course.setCategory(updatedCourse.getCategory());
                    course.setStatus(updatedCourse.getStatus());
                    course.setAdminReviewComment(updatedCourse.getAdminReviewComment());
                    
                    if (updatedCourse.getLessons() != null) {
                        course.getLessons().clear();
                        course.getLessons().addAll(updatedCourse.getLessons());
                    }
                    generateAiCourseSummary(course);
                    Course saved = courseRepository.save(course);
                    logActivity(course.getInstructor(), "Updated course: " + course.getTitle() + " (Status: " + course.getStatus() + ")");
                    return saved;
                }).orElse(null);
    }

    public Course approveCourse(Long id, String status, String comment) {
        return courseRepository.findById(id).map(course -> {
            course.setStatus(status);
            course.setAdminReviewComment(comment);
            Course saved = courseRepository.save(course);
            logActivity(course.getInstructor(), "Course '" + course.getTitle() + "' was " + status.toLowerCase() + " by Admin.");
            return saved;
        }).orElse(null);
    }

    private void generateAiCourseSummary(Course course) {
        if (course.getLessons() == null || course.getLessons().isEmpty()) {
            course.setCourseAiSummary("This course doesn't have any lessons yet.");
            return;
        }

        StringBuilder summary = new StringBuilder("This course provides a comprehensive learning journey starting with ");
        summary.append(course.getLessons().get(0).getTitle()).append(". ");
        
        if (course.getLessons().size() > 1) {
            summary.append("Students will then progress through key topics such as ");
            for (int i = 1; i < Math.min(course.getLessons().size(), 4); i++) {
                summary.append(course.getLessons().get(i).getTitle());
                if (i < Math.min(course.getLessons().size(), 4) - 1) {
                    summary.append(", ");
                }
            }
            summary.append(". ");
        }

        summary.append("By the end of this curriculum, learners will have mastered the fundamental and advanced concepts of ")
                .append(course.getCategory()).append(".");
        
        course.setCourseAiSummary(summary.toString());
    }

    public void deleteCourse(Long id) {
        courseRepository.findById(id).ifPresent(course -> {
            String instructor = course.getInstructor();
            String title = course.getTitle();
            courseRepository.deleteById(id);
            logActivity(instructor, "Deleted course: " + title);
        });
    }
}