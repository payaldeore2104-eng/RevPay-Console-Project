package com.revpay.model;

import java.sql.Timestamp;

public class Transaction {
    private int transactionId;
    private int senderId;
    private int receiverId;
    private double amount;
    private String type; // SEND, RECEIVE, ADD, WITHDRAW
    private String status; // SUCCESS, FAILED, PENDING
    private Timestamp timestamp;

    public Transaction() {
    }

    public Transaction(int transactionId, int senderId, int receiverId, double amount, String type, String status,
            Timestamp timestamp) {
        this.transactionId = transactionId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public String toString() {
        return String.format("ID: %d | From: %d | To: %d | Amount: %.2f | Type: %s | Status: %s | Date: %s",
                transactionId, senderId, receiverId, amount, type, status, timestamp);
    }
}
