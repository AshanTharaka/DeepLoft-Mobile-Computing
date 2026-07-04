package com.deeploft.backend.controller;

import com.deeploft.backend.model.ActivityLog;
import com.deeploft.backend.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityLogController {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @GetMapping("/{instructorName}")
    public List<ActivityLog> getLogs(@PathVariable String instructorName) {
        return activityLogRepository.findByInstructorNameOrderByTimestampDesc(instructorName);
    }
}