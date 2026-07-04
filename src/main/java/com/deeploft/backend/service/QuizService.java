package com.deeploft.backend.service;

import com.deeploft.backend.model.Quiz;
import com.deeploft.backend.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    public Quiz getQuizByCourse(Long courseId) {
        return quizRepository.findByCourseId(courseId).orElse(null);
    }
}