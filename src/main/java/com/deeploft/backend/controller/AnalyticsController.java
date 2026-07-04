package com.deeploft.backend.controller;

import com.deeploft.backend.model.SalesAnalytics;
import com.deeploft.backend.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/{instructorName}")
    public List<SalesAnalytics> getAnalytics(@PathVariable String instructorName) {
        return analyticsService.getInstructorAnalytics(instructorName);
    }
}