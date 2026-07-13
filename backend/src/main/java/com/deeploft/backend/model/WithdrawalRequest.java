package com.deeploft.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String instructorName;
    private String instructorEmail;
    private double amount;
    private String bankName;
    private String bankAccountNumber;
    private LocalDateTime requestDate;
    private String status; // "PENDING", "APPROVED", "REJECTED"
    private String adminComment;
}