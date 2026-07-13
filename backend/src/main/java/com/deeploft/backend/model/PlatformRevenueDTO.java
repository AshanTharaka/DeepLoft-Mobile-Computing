package com.deeploft.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformRevenueDTO {
    private double totalRevenue;
    private double totalCommission;
    private double totalInstructorPayouts;
    private int totalEnrollments;
    private List<SalesAnalytics> monthlyRevenue;
}