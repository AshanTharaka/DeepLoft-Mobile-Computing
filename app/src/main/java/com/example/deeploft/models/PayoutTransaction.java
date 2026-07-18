package com.example.deeploft.models;

import java.io.Serializable;

public class PayoutTransaction implements Serializable {
    private Long id;
    private String recipientName;
    private String recipientRole;
    private String bankName;
    private String bankAccountNumber;
    private double amount;
    private String timestamp;
    private String status;
    private String reference;

    public String getRecipientName() { return recipientName; }
    public String getBankName() { return bankName; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public double getAmount() { return amount; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public String getReference() { return reference; }
}