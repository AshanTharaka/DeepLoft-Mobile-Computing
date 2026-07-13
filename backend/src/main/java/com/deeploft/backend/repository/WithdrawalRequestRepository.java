package com.deeploft.backend.repository;

import com.deeploft.backend.model.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {
    List<WithdrawalRequest> findByInstructorEmail(String email);
    List<WithdrawalRequest> findByStatus(String status);
}