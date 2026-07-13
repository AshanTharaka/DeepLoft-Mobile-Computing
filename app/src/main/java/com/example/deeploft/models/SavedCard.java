package com.example.deeploft.models;

import java.io.Serializable;

public class SavedCard implements Serializable {
    private Long id;
    private String userEmail;
    private String cardHolderName;
    private String maskedCardNumber;
    private String expiryDate;
    private String cardType;

    public SavedCard() {}

    public SavedCard(String userEmail, String cardHolderName, String maskedCardNumber, String expiryDate, String cardType) {
        this.userEmail = userEmail;
        this.cardHolderName = cardHolderName;
        this.maskedCardNumber = maskedCardNumber;
        this.expiryDate = expiryDate;
        this.cardType = cardType;
    }

    public Long getId() { return id; }
    public String getCardHolderName() { return cardHolderName; }
    public String getMaskedCardNumber() { return maskedCardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public String getCardType() { return cardType; }
}