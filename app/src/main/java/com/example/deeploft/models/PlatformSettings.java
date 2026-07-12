package com.example.deeploft.models;

public class PlatformSettings {
    private String ownerName;
    private String ownerBankName;
    private String ownerBankAccount;
    private double commissionPercentage;

    public String getOwnerBankName() { return ownerBankName; }
    public void setOwnerBankName(String ownerBankName) { this.ownerBankName = ownerBankName; }
    public String getOwnerBankAccount() { return ownerBankAccount; }
    public void setOwnerBankAccount(String ownerBankAccount) { this.ownerBankAccount = ownerBankAccount; }
    public double getCommissionPercentage() { return commissionPercentage; }
    public void setCommissionPercentage(double commissionPercentage) { this.commissionPercentage = commissionPercentage; }
}