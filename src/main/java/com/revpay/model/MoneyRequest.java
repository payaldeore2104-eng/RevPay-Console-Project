package com.revpay.model;

import java.sql.Timestamp;

public class MoneyRequest {
    private int requestId;
    private int senderId;
    private int receiverId;
    private double amount;
    private String status; // PENDING, ACCEPTED, DECLINED, CANCELLED
    private Timestamp timestamp;

    public MoneyRequest() {
    }

    public MoneyRequest(int requestId, int senderId, int receiverId, double amount, String status,
            Timestamp timestamp) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
