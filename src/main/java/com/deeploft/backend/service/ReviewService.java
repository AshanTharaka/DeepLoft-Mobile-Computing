package com.deeploft.backend.service;

import com.deeploft.backend.model.Review;
import com.deeploft.backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getReviewsByCourse(Long courseId) {
        return reviewRepository.findByCourseId(courseId);
    }

    public Review addReview(Review review) {
        review.setDate(LocalDateTime.now());
        return reviewRepository.save(review);
    }
}