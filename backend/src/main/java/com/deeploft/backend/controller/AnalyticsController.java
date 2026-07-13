package com.deeploft.backend.controller;

import com.deeploft.backend.model.SalesAnalytics;
import com.deeploft.backend.model.StudentProgressDTO;
import com.deeploft.backend.model.TeacherStatsDTO;
import com.deeploft.backend.model.PlatformRevenueDTO;
import com.deeploft.backend.model.PayoutTransaction;
import com.deeploft.backend.repository.PayoutTransactionRepository;
import com.deeploft.backend.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private PayoutTransactionRepository payoutTransactionRepository;

    @GetMapping("/{instructorName}")
    public List<SalesAnalytics> getAnalytics(@PathVariable String instructorName) {
        return analyticsService.getInstructorAnalytics(instructorName);
    }

    @GetMapping("/platform-revenue")
    public PlatformRevenueDTO getPlatformRevenue() {
        return analyticsService.getPlatformRevenueStats();
    }

    @GetMapping("/payouts/{name}")
    public List<PayoutTransaction> getPayouts(@PathVariable String name) {
        return payoutTransactionRepository.findByRecipientName(name);
    }

    @GetMapping("/teacher-stats/{instructorName}")
    public TeacherStatsDTO getTeacherStats(@PathVariable String instructorName) {
        return analyticsService.getTeacherStats(instructorName);
    }

    @GetMapping("/students/{instructorName}")
    public List<StudentProgressDTO> getStudentProgress(@PathVariable String instructorName) {
        return analyticsService.getStudentProgressForInstructor(instructorName);
    }
}