package com.deeploft.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformSettings {
    @Id
    private Long id = 1L; // Singleton settings
    
    private String ownerName = "DeepLoft Admin";
    private String ownerBankName = "Your Bank Name";
    private String ownerBankAccount = "Your Private Account Number";
    private double commissionPercentage = 20.0; // Default 20%
}