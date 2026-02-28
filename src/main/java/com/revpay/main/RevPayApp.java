package com.revpay.main;

import com.revpay.model.BusinessDetails;
import com.revpay.model.Invoice;
import com.revpay.model.LoanApplication;
import com.revpay.model.MoneyRequest;
import com.revpay.model.Notification;
import com.revpay.model.PaymentMethod;
import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.service.*;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class RevPayApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthService authService = new AuthService();
    private static final WalletService walletService = new WalletService();
    private static final TransactionService transactionService = new TransactionService();
    private static final PaymentService paymentService = new PaymentService();
    private static final NotificationService notificationService = new NotificationService();
    private static final BusinessService businessService = new BusinessService();
    private static final LoanService loanService = new LoanService();

    private static User currentUser = null;
    private static long lastActivityTime = System.currentTimeMillis();

    public static void main(String[] args) {
    	System.out.println("==============================================");
    	System.out.println("          WELCOME TO REVPAY");
    	
    	System.out.println("      Your Digital Finance Solution");
    	System.out.println("==============================================");
    	
    	System.out.println();

        while (true) {
            if (currentUser != null && (System.currentTimeMillis() - lastActivityTime > 5 * 60 * 1000)) {
                System.out.println("\nSession timed out due to inactivity.");
                logout();
            }

            if (currentUser == null) {
                showAuthMenu();
            } else {
                showUserMenu();
            }
        }

        
    }
    
    private static void showAuthMenu() {
    	System.out.println("                 MAIN MENU");
    	System.out.println("==============================================");
    	System.out.println("1. Login");
    	System.out.println("2. Register Personal Account");
    	System.out.println("3. Register Business Account");
    	System.out.println("4. Forgot Password");
    	System.out.println("5. Exit");
    	System.out.println("==============================================");
    	System.out.print("Select Option: ");
    	
        int choice = getIntInput();
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register(false);
                break;
            case 3:
                register(true);
                break;
            case 4:
                forgotPassword();
                break;
            case 5:
                System.out.println("Goodbye! Thank You for Using RevPay....");
                System.exit(0);
            default:
                System.out.println("Invalid option.");
        }
    }

    private static void showUserMenu() {
        System.out.println("\n----- DASHBOARD (" + currentUser.getFullName() + ") -----");
        System.out.println("1. Wallet Balance & Add/Withdraw");
        System.out.println("2. Send Money");
        System.out.println("3. Request Money / View Requests");
        System.out.println("4. Transaction History");
        System.out.println("5. Manage Cards");
        System.out.println("6. Notifications");
        System.out.println("7. Change Password");

        if ("BUSINESS".equalsIgnoreCase(currentUser.getAccountType())) {
            System.out.println("8. Business Tools");
            System.out.println("9. Loan Management");
            System.out.println("10. Logout");
        } else {
            System.out.println("8. Logout");
        }

        System.out.print("Select Option: ");
        int choice = getIntInput();

        if ("BUSINESS".equalsIgnoreCase(currentUser.getAccountType())) {
            switch (choice) {
                case 1:
                    handleWallet();
                    break;
                case 2:
                    handleSendMoney();
                    break;
                case 3:
                    handleRequests();
                    break;
                case 4:
                    viewHistory();
                    break;
                case 5:
                    manageCards();
                    break;
                case 6:
                    viewNotifications();
                    break;
                case 7:
                    changePassword();
                    break;
                case 8:
                    handleBusiness();
                    break;
                case 9:
                    handleLoans();
                    break;
                case 10:
                    logout();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } else {
            switch (choice) {
                case 1:
                    handleWallet();
                    break;
                case 2:
                    handleSendMoney();
                    break;
                case 3:
                    handleRequests();
                    break;
                case 4:
                    viewHistory();
                    break;
                case 5:
                    manageCards();
                    break;
                case 6:
                    viewNotifications();
                    break;
                case 7:
                    changePassword();
                    break;
                case 8:
                    logout();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void login() {
        System.out.print("Enter Email/Phone: ");
        String id = scanner.nextLine();
        System.out.print("Enter Password: ");
        String pass = scanner.nextLine();

        try {
            currentUser = authService.login(id, pass);
            System.out.println("Login successful!");
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static void register(boolean isBusiness) {

        User user = new User();

        System.out.print("Full Name: ");
        user.setFullName(scanner.nextLine());

        System.out.print("Username: ");
        user.setUsername(scanner.nextLine());

        System.out.print("Email: ");
        user.setEmail(scanner.nextLine());

        System.out.print("Phone: ");
        user.setPhone(scanner.nextLine());

        System.out.print("Password: ");
        user.setHashedPassword(scanner.nextLine()); // Hashed in service


        // Standard Fixed Security Questions
        System.out.println("Security Question 1: What is your pet name?");
        user.setSecurityAnswer1(scanner.nextLine().trim());

        System.out.println("Security Question 2: What is your birthplace?");
        user.setSecurityAnswer2(scanner.nextLine().trim());

        System.out.println("Security Question 3: What is your favorite movie?");
        user.setSecurityAnswer3(scanner.nextLine().trim());


        System.out.print("Transaction PIN: ");
        user.setTransactionPin(scanner.nextLine());

        user.setAccountType(isBusiness ? "BUSINESS" : "PERSONAL");


        BusinessDetails details = null;

        if (isBusiness) {
            details = new BusinessDetails();

            System.out.print("Business Name: ");
            details.setBusinessName(scanner.nextLine());

            System.out.print("Business Type: ");
            details.setBusinessType(scanner.nextLine());

            System.out.print("Tax ID: ");
            details.setTaxId(scanner.nextLine());

            System.out.print("Address: ");
            details.setAddress(scanner.nextLine());

            System.out.print("Doc Path: ");
            details.setVerificationDocumentPath(scanner.nextLine());
        }

        if (authService.registerUser(user, details)) {
            System.out.println("Registration successful! Please login.");
        } else {
            System.out.println("Registration failed. User may already exist.");
        }
    }

   
    private static void forgotPassword() {

        System.out.print("Enter Email/Phone/Username: ");
        String id = scanner.nextLine();

        // Ask all 3 standard questions
        System.out.println("Security Question 1: What is your pet name?");
        String answer1 = scanner.nextLine();

        System.out.println("Security Question 2: What is your birthplace?");
        String answer2 = scanner.nextLine();

        System.out.println("Security Question 3: What is your favorite movie?");
        String answer3 = scanner.nextLine();

        System.out.print("Enter New Password: ");
        String newPass = scanner.nextLine();

        if (authService.resetPassword(id, answer1, answer2, answer3, newPass)) {
            System.out.println("Password reset successful.");
        } else {
            System.out.println("Reset failed. Invalid answers or user not found.");
        }
    }

    private static void handleWallet() {
        System.out.println("Current Balance: " + walletService.getBalance(currentUser.getUserId()));
        System.out.println("1. Add Money");
        System.out.println("2. Withdraw Money");
        System.out.println("3. Back");
        int choice = getIntInput();
        if (choice == 1) {
            System.out.print("Amount: ");
            double amt = getDoubleInput();
            if (walletService.addMoney(currentUser.getUserId(), amt)) {
                System.out.println("Money added.");
                notificationService.sendNotification(currentUser.getUserId(), "Added " + amt + " to wallet",
                        "TRANSACTION");
            } else
                System.out.println("Failed.");
        } else if (choice == 2) {
            System.out.print("Amount: ");
            double amt = getDoubleInput();
            System.out.print("Enter Transaction PIN: ");
            String pin = scanner.nextLine();
            if (!pin.equals(currentUser.getTransactionPin())) {
                System.out.println("Invalid PIN.");
                return;
            }
            if (walletService.withdrawMoney(currentUser.getUserId(), amt)) {
                System.out.println("Money withdrawn.");
                notificationService.sendNotification(currentUser.getUserId(), "Withdrew " + amt + " from wallet",
                        "TRANSACTION");
            } else
                System.out.println("Failed (Insufficient funds or invalid amount).");
        }
    }

    private static void handleSendMoney() {
        System.out.print("Receiver Email/Phone/ID: ");
        String rcvr = scanner.nextLine();
        System.out.print("Amount: ");
        double amt = getDoubleInput();

        System.out.print("Enter Transaction PIN: ");
        String pin = scanner.nextLine();
        if (!pin.equals(currentUser.getTransactionPin())) {
            System.out.println("Invalid PIN.");
            return;
        }

        if (transactionService.sendMoney(currentUser.getUserId(), rcvr, amt)) {
            System.out.println("Transfer successful!");
            notificationService.sendNotification(currentUser.getUserId(), "Sent " + amt + " to " + rcvr, "TRANSACTION");
            // Assuming transactionService finds user, we can't easily notify receiver here
            // without fetching their ID again inside service.
            // Service handles logging, maybe service should notify?
            // For simplicity, we only notify logged in user here or let service/db triggers
            // handle it.
            // Let's stick to simple dashboard notifications.
        } else {
            System.out.println("Transfer failed.");
        }
    }

    private static void handleRequests() {
        System.out.println("1. Request Money");
        System.out.println("2. View Pending Requests");
        int choice = getIntInput();
        if (choice == 1) {
            System.out.print("Payer Email/Phone: ");
            String who = scanner.nextLine();
            System.out.print("Amount: ");
            double amt = getDoubleInput();
            if (transactionService.requestMoney(currentUser.getUserId(), who, amt)) {
                System.out.println("Request sent.");
            } else {
                System.out.println("Failed.");
            }
        } else if (choice == 2) {
            List<MoneyRequest> reqs = transactionService.getPendingRequests(currentUser.getUserId());
            if (reqs.isEmpty())
                System.out.println("No pending requests.");
            for (MoneyRequest r : reqs) {
                System.out.println(
                        "ID: " + r.getRequestId() + " | From: " + r.getSenderId() + " | Amt: " + r.getAmount());
                System.out.print("Accept (Y/N)? ");
                String yn = scanner.nextLine();
                if (yn.equalsIgnoreCase("Y")) {
                    if (transactionService.processMoneyRequest(currentUser.getUserId(), r.getRequestId(), true)) {
                        System.out.println("Accepted & Paid.");
                    } else
                        System.out.println("Failed (Check Balance).");
                } else if (yn.equalsIgnoreCase("N")) {
                    transactionService.processMoneyRequest(currentUser.getUserId(), r.getRequestId(), false);
                    System.out.println("Declined.");
                }
            }
        }
    }

    private static void viewHistory() {
        List<Transaction> list = transactionService.getTransactionHistory(currentUser.getUserId());
        for (Transaction t : list) {
            System.out.println(t);
        }
    }

    private static void manageCards() {
        System.out.println("1. Add Card");
        System.out.println("2. View Cards");
        System.out.println("3. Remove Card");
        int c = getIntInput();
        if (c == 1) {
            System.out.print("Card No: ");
            String no = scanner.nextLine();
            System.out.print("Expiry (MM/YY): ");
            String exp = scanner.nextLine();
            System.out.print("Type (CREDIT/DEBIT): ");
            String type = scanner.nextLine();
            paymentService.addCard(currentUser.getUserId(), no, exp, type);
            System.out.println("Card added.");
        } else if (c == 2) {
            List<PaymentMethod> list = paymentService.getUserCards(currentUser.getUserId());
            for (PaymentMethod p : list)
                System.out.println("ID: " + p.getMethodId() + " | Type: " + p.getType() + " | **** **** **** "
                        + p.getCardNumberEncrypted().substring(p.getCardNumberEncrypted().length() - 4));
            // Decryption logic not fully implemented for display masked, assume helper or
            // just show ID
        } else if (c == 3) {
            System.out.print("Card ID: ");
            int id = getIntInput();
            paymentService.removeCard(id);
            System.out.println("Removed.");
        }
    }

    private static void viewNotifications() {
        List<Notification> list = notificationService.getUserNotifications(currentUser.getUserId());
        for (Notification n : list) {
            System.out.println((n.isRead() ? "[Read]" : "[New]") + " " + n.getMessage());
        }
        notificationService.markAllRead(currentUser.getUserId());
    }

    private static void handleBusiness() {
        System.out.println("1. Create Invoice");
        System.out.println("2. View Invoices");
        System.out.println("3. View Analytics");
        int c = getIntInput();
        if (c == 1) {
            System.out.print("Customer Name: ");
            String name = scanner.nextLine();
            System.out.print("Items: ");
            String items = scanner.nextLine();
            System.out.print("Amount: ");
            double amt = getDoubleInput();
            businessService.createInvoice(currentUser.getUserId(), name, items, amt,
                    new Date(System.currentTimeMillis() + 86400000L)); // +1 day
            System.out.println("Invoice created.");
        } else if (c == 2) {
            List<Invoice> list = businessService.getBusinessInvoices(currentUser.getUserId());
            for (Invoice i : list)
                System.out.println("ID: " + i.getInvoiceId() + " | To: " + i.getCustomerName() + " | Amt: "
                        + i.getTotalAmount() + " | Status: " + i.getStatus());
        } else if (c == 3) {
            System.out.println(businessService.getBusinessAnalytics(currentUser.getUserId()));
        }
    }

    private static void changePassword() {
        System.out.print("Enter Current Password: ");
        String currentPass = scanner.nextLine();
        System.out.print("Enter Transaction PIN: ");
        String pin = scanner.nextLine();
        System.out.print("Enter New Password: ");
        String newPass = scanner.nextLine();
        System.out.print("Confirm New Password: ");
        String confirmPass = scanner.nextLine();

        if (!newPass.equals(confirmPass)) {
            System.out.println("Passwords do not match!");
            return;
        }

        if (authService.changePassword(currentUser.getEmail(), currentPass, pin, newPass)) {
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Password change failed. Check your current password and PIN.");
        }
    }

    private static void handleLoans() {
        System.out.println("\n----- LOAN MANAGEMENT -----");
        System.out.println("1. Apply for Loan");
        System.out.println("2. View My Loans");
        System.out.println("3. Make Loan Payment");
        System.out.println("4. Loan Statistics");
        System.out.println("5. Back");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                applyForLoan();
                break;
            case 2:
                viewLoans();
                break;
            case 3:
                makeLoanPayment();
                break;
            case 4:
                System.out.println(loanService.getLoanStatistics(currentUser.getUserId()));
                break;
            case 5:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    private static void applyForLoan() {
        System.out.print("Loan Amount (10,000 - 5,000,000): ");
        double amount = getDoubleInput();
        System.out.print("Purpose: ");
        String purpose = scanner.nextLine();
        System.out.print("Duration (months, 6-60): ");
        int duration = getIntInput();

        if (loanService.applyForLoan(currentUser.getUserId(), amount, purpose, duration)) {
            System.out.println("Loan application submitted successfully!");
            System.out.println("Your application is pending review.");
            notificationService.sendNotification(currentUser.getUserId(),
                    "Loan application submitted for amount: " + amount, "LOAN");
        } else {
            System.out.println("Loan application failed. Check the amount and duration requirements.");
        }
    }

    private static void viewLoans() {
        List<LoanApplication> loans = loanService.getUserLoans(currentUser.getUserId());
        if (loans.isEmpty()) {
            System.out.println("No loans found.");
            return;
        }

        for (LoanApplication loan : loans) {
            System.out.println("\n----------------------------------");
            System.out.println("Loan ID: " + loan.getLoanId());
            System.out.println("Amount: " + loan.getAmount());
            System.out.println("Interest Rate: " + loan.getInterestRate() + "%");
            System.out.println("Duration: " + loan.getDurationMonths() + " months");
            System.out.println("Status: " + loan.getStatus());
            System.out.println("Total Due: " + loan.getTotalAmountDue());
            System.out.println("Paid: " + loan.getAmountPaid());
            System.out.println("Balance: " + loan.getBalanceRemaining());
            System.out.println("Applied: " + loan.getAppliedDate());
        }
    }

    private static void makeLoanPayment() {
        System.out.print("Loan ID: ");
        int loanId = getIntInput();
        System.out.print("Payment Amount: ");
        double amount = getDoubleInput();

        System.out.print("Enter Transaction PIN: ");
        String pin = scanner.nextLine();
        if (!pin.equals(currentUser.getTransactionPin())) {
            System.out.println("Invalid PIN.");
            return;
        }

        // Check wallet balance
        double balance = walletService.getBalance(currentUser.getUserId());
        if (balance < amount) {
            System.out.println("Insufficient wallet balance. Current balance: " + balance);
            return;
        }

        // Deduct from wallet and make payment
        if (walletService.withdrawMoney(currentUser.getUserId(), amount)) {
            if (loanService.makePayment(loanId, amount)) {
                System.out.println("Loan payment successful!");
                notificationService.sendNotification(currentUser.getUserId(),
                        "Loan payment of " + amount + " processed for loan ID: " + loanId, "LOAN");
            } else {
                // Refund to wallet if payment failed
                walletService.addMoney(currentUser.getUserId(), amount);
                System.out.println("Loan payment failed.");
            }
        } else {
            System.out.println("Failed to deduct amount from wallet.");
        }
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out.");
    }

    private static int getIntInput() {
        lastActivityTime = System.currentTimeMillis();
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double getDoubleInput() {
        lastActivityTime = System.currentTimeMillis();
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
