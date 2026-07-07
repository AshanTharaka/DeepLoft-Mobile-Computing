package com.deeploft.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String userEmail;
    private String cardHolderName;
    private String maskedCardNumber; // e.g., **** **** **** 4242
    private String expiryDate;
    private String cardType; // "VISA", "MASTERCARD", etc.
}