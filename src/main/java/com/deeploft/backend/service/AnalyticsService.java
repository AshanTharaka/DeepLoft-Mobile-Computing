package com.deeploft.backend.service;

import com.deeploft.backend.model.Enrollment;
import com.deeploft.backend.model.SalesAnalytics;
import com.deeploft.backend.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

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
}