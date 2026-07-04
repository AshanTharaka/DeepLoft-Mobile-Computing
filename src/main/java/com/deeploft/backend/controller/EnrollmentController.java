package com.deeploft.backend.controller;

import com.deeploft.backend.model.Enrollment;
import com.deeploft.backend.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/{email}")
    public List<Enrollment> getEnrollments(@PathVariable String email) {
        return enrollmentService.getEnrollmentsByUser(email);
    }

    @PostMapping
    public Enrollment enroll(@RequestBody Enrollment enrollment) {
        return enrollmentService.enrollUser(enrollment);
    }
}