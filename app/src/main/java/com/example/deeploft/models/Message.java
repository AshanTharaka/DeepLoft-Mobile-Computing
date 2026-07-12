package com.example.deeploft.models;

import java.io.Serializable;

public class Message implements Serializable {
    private Long id;
    private String senderEmail;
    private String receiverEmail;
    private String content;
    private String timestamp;
    private boolean isRead;

    public Message(String senderEmail, String receiverEmail, String content) {
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.content = content;
    }

    public String getSenderEmail() { return senderEmail; }
    public String getReceiverEmail() { return receiverEmail; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
}