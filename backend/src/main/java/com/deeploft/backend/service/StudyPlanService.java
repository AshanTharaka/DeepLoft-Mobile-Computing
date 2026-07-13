package com.deeploft.backend.service;

import com.deeploft.backend.model.StudyPlan;
import com.deeploft.backend.repository.StudyPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class StudyPlanService {

    @Autowired
    private StudyPlanRepository studyPlanRepository;

    public Optional<StudyPlan> getStudyPlan(String email, String courseTitle) {
        return studyPlanRepository.findByUserEmailAndCourseTitle(email, courseTitle);
    }

    public StudyPlan generateStudyPlan(String email, String courseTitle, String goal) {
        StudyPlan plan = new StudyPlan();
        plan.setUserEmail(email);
        plan.setCourseTitle(courseTitle);
        plan.setGoal(goal);

        // AI Simulated Logic for generating tasks based on goal
        List<String> tasks = new ArrayList<>();
        if (goal.toLowerCase().contains("2 weeks") || goal.toLowerCase().contains("fast")) {
            tasks.addAll(Arrays.asList(
                "Day 1-2: Master Fundamentals and Setup",
                "Day 3-5: Complete Core Modules",
                "Day 6-8: Hands-on Projects",
                "Day 9-12: Advanced Topics and Optimization",
                "Day 13-14: Final Review and Certification"
            ));
        } else {
            tasks.addAll(Arrays.asList(
                "Week 1: Foundations and History",
                "Week 2: Deep Dive into Tooling",
                "Week 3: Practical Application - Phase 1",
                "Week 4: Practical Application - Phase 2",
                "Week 5: Project Review and Improvements",
                "Week 6: Final Assessment Preparation"
            ));
        }
        plan.setDailyTasks(tasks);

        // Save or update
        return studyPlanRepository.findByUserEmailAndCourseTitle(email, courseTitle)
                .map(existing -> {
                    existing.setGoal(goal);
                    existing.setDailyTasks(tasks);
                    return studyPlanRepository.save(existing);
                })
                .orElseGet(() -> studyPlanRepository.save(plan));
    }
}