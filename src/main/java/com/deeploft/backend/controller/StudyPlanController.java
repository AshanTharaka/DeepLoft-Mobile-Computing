package com.deeploft.backend.controller;

import com.deeploft.backend.model.StudyPlan;
import com.deeploft.backend.service.StudyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/study-plans")
public class StudyPlanController {

    @Autowired
    private StudyPlanService studyPlanService;

    @GetMapping("/{email}/{courseTitle}")
    public Optional<StudyPlan> getStudyPlan(@PathVariable String email, @PathVariable String courseTitle) {
        return studyPlanService.getStudyPlan(email, courseTitle);
    }

    @PostMapping("/generate")
    public StudyPlan generate(@RequestBody StudyPlan request) {
        return studyPlanService.generateStudyPlan(request.getUserEmail(), request.getCourseTitle(), request.getGoal());
    }
}