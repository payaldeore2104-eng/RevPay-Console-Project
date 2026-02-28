package com.revpay.model;

import java.sql.Timestamp;

/**
 * Represents a loan application from a business user
 */
public class LoanApplication {
    private int loanId;
    private int userId;
    private double amount;
    private String purpose;
    private double interestRate;
    private int durationMonths;
    private String status; // PENDING, APPROVED, REJECTED, ACTIVE, CLOSED
    private Timestamp appliedDate;
    private Timestamp approvedDate;
    private double amountPaid;

    public LoanApplication() {
        this.status = "PENDING";
        this.interestRate = 8.5; // Default interest rate
        this.amountPaid = 0.0;
    }

    public LoanApplication(int loanId, int userId, double amount, String purpose, double interestRate,
            int durationMonths, String status, Timestamp appliedDate, Timestamp approvedDate) {
        this.loanId = loanId;
        this.userId = userId;
        this.amount = amount;
        this.purpose = purpose;
        this.interestRate = interestRate;
        this.durationMonths = durationMonths;
        this.status = status;
        this.appliedDate = appliedDate;
        this.approvedDate = approvedDate;
        this.amountPaid = 0.0;
    }

    // Getters and Setters
    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Timestamp appliedDate) {
        this.appliedDate = appliedDate;
    }

    public Timestamp getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Timestamp approvedDate) {
        this.approvedDate = approvedDate;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getTotalAmountDue() {
        return amount + (amount * interestRate / 100);
    }

    public double getBalanceRemaining() {
        return getTotalAmountDue() - amountPaid;
    }

    @Override
    public String toString() {
        return "LoanApplication{" +
                "loanId=" + loanId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", purpose='" + purpose + '\'' +
                ", interestRate=" + interestRate +
                ", durationMonths=" + durationMonths +
                ", status='" + status + '\'' +
                ", appliedDate=" + appliedDate +
                ", approvedDate=" + approvedDate +
                ", amountPaid=" + amountPaid +
                ", totalDue=" + getTotalAmountDue() +
                '}';
    }
}
