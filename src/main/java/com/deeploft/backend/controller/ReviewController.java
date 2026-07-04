package com.deeploft.backend.controller;

import com.deeploft.backend.model.Review;
import com.deeploft.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/course/{courseId}")
    public List<Review> getReviewsByCourse(@PathVariable Long courseId) {
        return reviewService.getReviewsByCourse(courseId);
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }
}