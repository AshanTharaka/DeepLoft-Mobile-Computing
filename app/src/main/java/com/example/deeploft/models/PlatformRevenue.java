package com.example.deeploft.models;

import java.io.Serializable;
import java.util.List;

public class PlatformRevenue implements Serializable {
    private double totalRevenue;
    private double totalCommission;
    private double totalInstructorPayouts;
    private int totalEnrollments;
    private List<SalesAnalytics> monthlyRevenue;

    public double getTotalRevenue() { return totalRevenue; }
    public double getTotalCommission() { return totalCommission; }
    public double getTotalInstructorPayouts() { return totalInstructorPayouts; }
    public int getTotalEnrollments() { return totalEnrollments; }
    public List<SalesAnalytics> getMonthlyRevenue() { return monthlyRevenue; }
}