package com.deeploft.backend.service;

import com.deeploft.backend.model.ActivityLog;
import com.deeploft.backend.model.Enrollment;
import com.deeploft.backend.repository.ActivityLogRepository;
import com.deeploft.backend.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public List<Enrollment> getEnrollmentsByUser(String email) {
        return enrollmentRepository.findByUserEmail(email);
    }

    public Enrollment enrollUser(Enrollment enrollment) {
        double total = enrollment.getAmountPaid();
        double commissionRate = 0.20; // 20% platform commission
        
        enrollment.setPlatformCommission(total * commissionRate);
        enrollment.setInstructorEarnings(total * (1 - commissionRate));
        enrollment.setEnrollmentDate(LocalDateTime.now());
        
        Enrollment saved = enrollmentRepository.save(enrollment);
        
        // Log the activity for the instructor
        ActivityLog log = new ActivityLog();
        log.setMessage("New enrollment in '" + enrollment.getCourse().getTitle() + "'. You earned $" + String.format("%.2f", enrollment.getInstructorEarnings()));
        log.setTimestamp(LocalDateTime.now());
        log.setInstructorName(enrollment.getCourse().getInstructor());
        activityLogRepository.save(log);
        
        return saved;
    }
}