package com.example.deeploft.models;

import java.io.Serializable;

public class WithdrawalRequest implements Serializable {
    private Long id;
    private String instructorName;
    private String instructorEmail;
    private double amount;
    private String bankName;
    private String bankAccountNumber;
    private String requestDate;
    private String status; // "PENDING", "APPROVED", "REJECTED"
    private String adminComment;

    public Long getId() { return id; }
    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }
    public String getInstructorEmail() { return instructorEmail; }
    public void setInstructorEmail(String instructorEmail) { this.instructorEmail = instructorEmail; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    public String getStatus() { return status; }
}