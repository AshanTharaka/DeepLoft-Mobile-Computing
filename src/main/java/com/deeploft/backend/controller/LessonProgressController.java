package com.deeploft.backend.controller;

import com.deeploft.backend.model.LessonProgress;
import com.deeploft.backend.repository.LessonProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class LessonProgressController {

    @Autowired
    private LessonProgressRepository progressRepository;

    @GetMapping("/{email}/{courseId}")
    public List<LessonProgress> getProgress(@PathVariable String email, @PathVariable Long courseId) {
        return progressRepository.findByUserEmailAndCourseId(email, courseId);
    }

    @PostMapping
    public LessonProgress updateProgress(@RequestBody LessonProgress progress) {
        return progressRepository.findByUserEmailAndLessonId(progress.getUserEmail(), progress.getLessonId())
                .map(existing -> {
                    existing.setCompleted(progress.isCompleted());
                    return progressRepository.save(existing);
                })
                .orElseGet(() -> progressRepository.save(progress));
    }
}