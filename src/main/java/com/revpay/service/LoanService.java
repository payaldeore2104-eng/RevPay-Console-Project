package com.revpay.service;

import com.revpay.model.LoanApplication;
import com.revpay.repository.LoanRepository;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class LoanService {
    private static final Logger logger = LoggerUtil.getLogger(LoanService.class);
    private final LoanRepository loanRepository = new LoanRepository();

    
    private static final double MIN_LOAN_AMOUNT = 10000.0;
    private static final double MAX_LOAN_AMOUNT = 5000000.0;
    private static final int MIN_DURATION = 6;
    private static final int MAX_DURATION = 60; 

    
    public boolean applyForLoan(int userId, double amount, String purpose, int durationMonths) {
        
        if (amount < MIN_LOAN_AMOUNT || amount > MAX_LOAN_AMOUNT) {
            logger.warn("Loan amount out of range for user " + userId);
            return false;
        }

        if (durationMonths < MIN_DURATION || durationMonths > MAX_DURATION) {
            logger.warn("Loan duration out of range for user " + userId);
            return false;
        }

        if (purpose == null || purpose.trim().isEmpty()) {
            logger.warn("Loan purpose is required for user " + userId);
            return false;
        }

    
        LoanApplication loan = new LoanApplication();
        loan.setUserId(userId);
        loan.setAmount(amount);
        loan.setPurpose(purpose);
        loan.setDurationMonths(durationMonths);
        loan.setInterestRate(calculateInterestRate(amount, durationMonths));
        loan.setStatus("PENDING");

        boolean success = loanRepository.createLoanApplication(loan);
        if (success) {
            logger.info("Loan application submitted for user " + userId + " amount: " + amount);
        }
        return success;
    }

    
    public List<LoanApplication> getUserLoans(int userId) {
        return loanRepository.getLoansByUserId(userId);
    }

    
    public LoanApplication getLoanById(int loanId) {
        return loanRepository.getLoanById(loanId);
    }

   
    public boolean approveLoan(int loanId) {
        return loanRepository.updateLoanStatus(loanId, "APPROVED");
    }

    /**
     * Reject a loan
     */
    public boolean rejectLoan(int loanId) {
        return loanRepository.updateLoanStatus(loanId, "REJECTED");
    }

    /**
     * Make a loan payment
     */
    public boolean makePayment(int loanId, double paymentAmount) {
        if (paymentAmount <= 0) {
            logger.warn("Invalid payment amount: " + paymentAmount);
            return false;
        }

        LoanApplication loan = loanRepository.getLoanById(loanId);
        if (loan == null) {
            logger.warn("Loan not found: " + loanId);
            return false;
        }

        if (!"APPROVED".equals(loan.getStatus()) && !"ACTIVE".equals(loan.getStatus())) {
            logger.warn("Cannot make payment on loan with status: " + loan.getStatus());
            return false;
        }

        // Update status to ACTIVE on first payment
        if ("APPROVED".equals(loan.getStatus())) {
            loanRepository.updateLoanStatus(loanId, "ACTIVE");
        }

        return loanRepository.recordPayment(loanId, paymentAmount);
    }

    /**
     * Calculate interest rate based on amount and duration
     */
    private double calculateInterestRate(double amount, int durationMonths) {
        // Simple interest rate calculation
        // Higher amount and longer duration = higher rate
        double baseRate = 8.5;

        if (amount > 1000000) {
            baseRate += 1.5;
        } else if (amount > 500000) {
            baseRate += 1.0;
        }

        if (durationMonths > 36) {
            baseRate += 1.0;
        } else if (durationMonths > 24) {
            baseRate += 0.5;
        }

        return Math.min(baseRate, 15.0); // Cap at 15%
    }

    /**
     * Get loan statistics for a user
     */
    public String getLoanStatistics(int userId) {
        List<LoanApplication> loans = getUserLoans(userId);

        int totalLoans = loans.size();
        int activeLoans = 0;
        double totalBorrowed = 0;
        double totalOutstanding = 0;

        for (LoanApplication loan : loans) {
            if ("ACTIVE".equals(loan.getStatus()) || "APPROVED".equals(loan.getStatus())) {
                activeLoans++;
                totalOutstanding += loan.getBalanceRemaining();
            }
            if (!"REJECTED".equals(loan.getStatus())) {
                totalBorrowed += loan.getAmount();
            }
        }

        return String.format("Total Loans: %d | Active: %d | Borrowed: %.2f | Outstanding: %.2f",
                totalLoans, activeLoans, totalBorrowed, totalOutstanding);
    }
}
