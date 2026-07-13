package com.deeploft.backend.controller;

import com.deeploft.backend.model.Quiz;
import com.deeploft.backend.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/course/{courseId}")
    public Quiz getQuizByCourse(@PathVariable Long courseId) {
        return quizService.getQuizByCourse(courseId);
    }
}