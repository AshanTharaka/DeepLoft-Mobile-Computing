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
public class PayoutTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String recipientName;
    private String recipientRole; // "ADMIN" or "INSTRUCTOR"
    private String bankName;
    private String bankAccountNumber;
    private double amount;
    private LocalDateTime timestamp;
    private String status; // "COMPLETED"
    private String reference; // "Enrollment in Course X"
}