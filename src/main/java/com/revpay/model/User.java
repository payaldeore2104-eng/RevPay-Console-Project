package com.revpay.model;

import java.sql.Timestamp;

public class User {

    private int userId;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String hashedPassword;

    // Only answers (standard 3 fixed questions)
    private String securityAnswer1;
    private String securityAnswer2;
    private String securityAnswer3;

    private int failedAttempts;
    private boolean accountLocked;
    private String transactionPin;
    private String accountType; // PERSONAL, BUSINESS
    private Timestamp createdAt;

    public User() {
    }

    // Simplified constructor (removed old securityQuestion fields)
    public User(int userId, String fullName, String username, String email, String phone,
                String hashedPassword,
                int failedAttempts, boolean accountLocked,
                String transactionPin, String accountType,
                Timestamp createdAt) {

        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.hashedPassword = hashedPassword;
        this.failedAttempts = failedAttempts;
        this.accountLocked = accountLocked;
        this.transactionPin = transactionPin;
        this.accountType = accountType;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSecurityAnswer1() {
        return securityAnswer1;
    }

    public void setSecurityAnswer1(String securityAnswer1) {
        this.securityAnswer1 = securityAnswer1;
    }

    public String getSecurityAnswer2() {
        return securityAnswer2;
    }

    public void setSecurityAnswer2(String securityAnswer2) {
        this.securityAnswer2 = securityAnswer2;
    }

    public String getSecurityAnswer3() {
        return securityAnswer3;
    }

    public void setSecurityAnswer3(String securityAnswer3) {
        this.securityAnswer3 = securityAnswer3;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public String getTransactionPin() {
        return transactionPin;
    }

    public void setTransactionPin(String transactionPin) {
        this.transactionPin = transactionPin;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", accountType='" + accountType + '\'' +
                '}';
    }
}