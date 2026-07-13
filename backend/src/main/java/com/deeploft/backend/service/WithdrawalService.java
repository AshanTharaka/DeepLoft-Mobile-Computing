package com.deeploft.backend.service;

import com.deeploft.backend.model.ActivityLog;
import com.deeploft.backend.model.Enrollment;
import com.deeploft.backend.model.WithdrawalRequest;
import com.deeploft.backend.repository.ActivityLogRepository;
import com.deeploft.backend.repository.EnrollmentRepository;
import com.deeploft.backend.repository.WithdrawalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawalService {

    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public double getInstructorBalance(String instructorName) {
        // Calculate total earnings from enrollments
        double totalEarnings = enrollmentRepository.findAll().stream()
                .filter(e -> e.getCourse().getInstructor().equalsIgnoreCase(instructorName))
                .mapToDouble(Enrollment::getInstructorEarnings)
                .sum();

        // Subtract total withdrawn (all except rejected)
        double totalWithdrawn = withdrawalRequestRepository.findAll().stream()
                .filter(w -> w.getInstructorName().equalsIgnoreCase(instructorName))
                .filter(w -> !"REJECTED".equalsIgnoreCase(w.getStatus()))
                .mapToDouble(WithdrawalRequest::getAmount)
                .sum();

        return totalEarnings - totalWithdrawn;
    }

    public WithdrawalRequest createRequest(WithdrawalRequest request) {
        request.setRequestDate(LocalDateTime.now());
        request.setStatus("PENDING");
        return withdrawalRequestRepository.save(request);
    }

    public List<WithdrawalRequest> getAllPending() {
        return withdrawalRequestRepository.findByStatus("PENDING");
    }

    public List<WithdrawalRequest> getByInstructor(String email) {
        return withdrawalRequestRepository.findByInstructorEmail(email);
    }

    public WithdrawalRequest updateStatus(Long id, String status, String comment) {
        return withdrawalRequestRepository.findById(id).map(w -> {
            w.setStatus(status);
            w.setAdminComment(comment);
            WithdrawalRequest saved = withdrawalRequestRepository.save(w);
            
            // Log for instructor
            ActivityLog log = new ActivityLog();
            log.setInstructorName(w.getInstructorName());
            log.setMessage("Withdrawal request for $" + String.format("%.2f", w.getAmount()) + " was " + status.toLowerCase());
            log.setTimestamp(LocalDateTime.now());
            activityLogRepository.save(log);
            
            return saved;
        }).orElse(null);
    }
}