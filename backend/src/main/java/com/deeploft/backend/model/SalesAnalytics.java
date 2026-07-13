package com.deeploft.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesAnalytics {
    private String month;
    private double totalSales;
    private int enrollmentCount;
}