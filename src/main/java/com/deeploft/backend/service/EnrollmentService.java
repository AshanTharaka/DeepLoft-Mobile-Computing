package com.deeploft.backend.service;

import com.deeploft.backend.model.*;
import com.deeploft.backend.repository.*;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlatformSettingsRepository platformSettingsRepository;

    @Autowired
    private PayoutTransactionRepository payoutTransactionRepository;

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
        
        // 1. Send Commission to Admin Bank Account
        sendCommissionToAdmin(enrollment);

        // 2. Send Earnings to Instructor Bank Account
        sendEarningsToInstructor(enrollment);
        
        // Log the activity for the instructor
        ActivityLog log = new ActivityLog();
        log.setMessage("New Student Enrollment: '" + enrollment.getCourse().getTitle() + "'. You earned $" + String.format("%.2f", enrollment.getInstructorEarnings()));
        log.setTimestamp(LocalDateTime.now());
        log.setInstructorName(enrollment.getCourse().getInstructor());
        activityLogRepository.save(log);
        
        return saved;
    }

    private void sendCommissionToAdmin(Enrollment enrollment) {
        PlatformSettings settings = platformSettingsRepository.findById(1L).orElse(new PlatformSettings());
        
        PayoutTransaction adminPayout = new PayoutTransaction();
        adminPayout.setRecipientName(settings.getOwnerName());
        adminPayout.setRecipientRole("ADMIN");
        adminPayout.setBankName(settings.getOwnerBankName());
        adminPayout.setBankAccountNumber(settings.getOwnerBankAccount());
        adminPayout.setAmount(enrollment.getPlatformCommission());
        adminPayout.setTimestamp(LocalDateTime.now());
        adminPayout.setStatus("COMPLETED");
        adminPayout.setReference("Platform Commission: " + enrollment.getCourse().getTitle());
        
        payoutTransactionRepository.save(adminPayout);
    }

    private void sendEarningsToInstructor(Enrollment enrollment) {
        String instructorName = enrollment.getCourse().getInstructor();
        userRepository.findAll().stream()
                .filter(u -> u.getName().equalsIgnoreCase(instructorName))
                .findFirst()
                .ifPresent(instructor -> {
                    PayoutTransaction instructorPayout = new PayoutTransaction();
                    instructorPayout.setRecipientName(instructor.getName());
                    instructorPayout.setRecipientRole("INSTRUCTOR");
                    instructorPayout.setBankName(instructor.getBankName());
                    instructorPayout.setBankAccountNumber(instructor.getBankAccountNumber());
                    instructorPayout.setAmount(enrollment.getInstructorEarnings());
                    instructorPayout.setTimestamp(LocalDateTime.now());
                    instructorPayout.setStatus("COMPLETED");
                    instructorPayout.setReference("Course Sale: " + enrollment.getCourse().getTitle());
                    
                    payoutTransactionRepository.save(instructorPayout);
                });
    }

    public void updateLastWatchedLesson(String email, Long courseId, Long lessonId) {
        enrollmentRepository.findAll().stream()
                .filter(e -> e.getUserEmail().equals(email) && e.getCourse().getId().equals(courseId))
                .findFirst()
                .ifPresent(e -> {
                    e.setLastWatchedLessonId(lessonId);
                    enrollmentRepository.save(e);
                });
    }
}