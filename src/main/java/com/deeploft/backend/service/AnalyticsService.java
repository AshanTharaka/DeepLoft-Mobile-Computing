package com.deeploft.backend.service;

import com.deeploft.backend.model.*;
import com.deeploft.backend.repository.CourseRepository;
import com.deeploft.backend.repository.EnrollmentRepository;
import com.deeploft.backend.repository.LessonProgressRepository;
import com.deeploft.backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public List<SalesAnalytics> getInstructorAnalytics(String instructorName) {
        List<Enrollment> enrollments = enrollmentRepository.findAll().stream()
                .filter(e -> e.getCourse().getInstructor().equals(instructorName))
                .collect(Collectors.toList());

        Map<String, SalesAnalytics> monthlyData = new LinkedHashMap<>();

        for (Enrollment e : enrollments) {
            String month = e.getEnrollmentDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            SalesAnalytics stats = monthlyData.getOrDefault(month, new SalesAnalytics(month, 0, 0));
            stats.setTotalSales(stats.getTotalSales() + e.getInstructorEarnings());
            stats.setEnrollmentCount(stats.getEnrollmentCount() + 1);
            monthlyData.put(month, stats);
        }

        return new ArrayList<>(monthlyData.values());
    }

    public TeacherStatsDTO getTeacherStats(String instructorName) {
        List<Course> courses = courseRepository.findAll().stream()
                .filter(c -> c.getInstructor().equalsIgnoreCase(instructorName))
                .collect(Collectors.toList());

        int totalStudents = (int) enrollmentRepository.findAll().stream()
                .filter(e -> e.getCourse().getInstructor().equalsIgnoreCase(instructorName))
                .count();

        double avgRating = reviewRepository.findAll().stream()
                .filter(r -> r.getCourse().getInstructor().equalsIgnoreCase(instructorName))
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        String aiBio = "As a highly rated educator on DeepLoft, " + instructorName + 
                       " specializes in " + (courses.isEmpty() ? "academic content" : courses.get(0).getCategory()) + 
                       ". With " + totalStudents + " students across " + courses.size() + 
                       " courses, they are known for their commitment to academic excellence.";

        return new TeacherStatsDTO(instructorName, "", totalStudents, avgRating, courses.size(), aiBio);
    }

    public List<StudentProgressDTO> getStudentProgressForInstructor(String instructorName) {
        List<Enrollment> enrollments = enrollmentRepository.findAll().stream()
                .filter(e -> e.getCourse().getInstructor().equals(instructorName))
                .collect(Collectors.toList());

        List<StudentProgressDTO> progressList = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            String studentEmail = enrollment.getUserEmail();
            Long courseId = enrollment.getCourse().getId();
            int totalLessons = enrollment.getCourse().getLessons().size();
            
            if (totalLessons == 0) continue;

            long completedLessons = lessonProgressRepository.findByUserEmailAndCourseId(studentEmail, courseId)
                    .stream().filter(LessonProgress::isCompleted).count();

            int percentage = (int) ((completedLessons * 100) / totalLessons);
            progressList.add(new StudentProgressDTO(studentEmail, enrollment.getCourse().getTitle(), percentage));
        }

        return progressList;
    }

    public PlatformRevenueDTO getPlatformRevenueStats() {
        List<Enrollment> allEnrollments = enrollmentRepository.findAll();
        
        double totalRev = allEnrollments.stream().mapToDouble(Enrollment::getAmountPaid).sum();
        double totalComm = allEnrollments.stream().mapToDouble(Enrollment::getPlatformCommission).sum();
        double totalPayouts = allEnrollments.stream().mapToDouble(Enrollment::getInstructorEarnings).sum();
        
        Map<String, SalesAnalytics> monthlyData = new LinkedHashMap<>();
        for (Enrollment e : allEnrollments) {
            String month = e.getEnrollmentDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            SalesAnalytics stats = monthlyData.getOrDefault(month, new SalesAnalytics(month, 0, 0));
            stats.setTotalSales(stats.getTotalSales() + e.getPlatformCommission());
            stats.setEnrollmentCount(stats.getEnrollmentCount() + 1);
            monthlyData.put(month, stats);
        }

        return new PlatformRevenueDTO(totalRev, totalComm, totalPayouts, allEnrollments.size(), new ArrayList<>(monthlyData.values()));
    }
}