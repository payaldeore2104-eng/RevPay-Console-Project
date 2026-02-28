package com.revpay.service;

import com.revpay.model.MoneyRequest;
import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.repository.DBConnection;
import com.revpay.repository.MoneyRequestRepository;
import com.revpay.repository.TransactionRepository;
import com.revpay.repository.UserRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TransactionService {
    private static final Logger logger = LoggerUtil.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final WalletRepository walletRepository = new WalletRepository();
    private final UserRepository userRepository = new UserRepository();
    private final MoneyRequestRepository moneyRequestRepository = new MoneyRequestRepository();
    private final NotificationService notificationService = new NotificationService();

    public boolean sendMoney(int senderId, String receiverIdentifier, double amount) {
        if (amount <= 0)
            return false;

        Optional<User> receiverOpt = userRepository.findByEmailPhoneOrUsername(receiverIdentifier);
        if (!receiverOpt.isPresent()) {
            // Try assuming it's an ID
            try {
                int receiverId = Integer.parseInt(receiverIdentifier);
                receiverOpt = userRepository.findById(receiverId);
            } catch (NumberFormatException ignored) {
            }
        }

        if (!receiverOpt.isPresent()) {
            logger.warn("Receiver not found: " + receiverIdentifier);
            return false;
        }

        int receiverId = receiverOpt.get().getUserId();
        if (senderId == receiverId) {
            logger.warn("Cannot send money to self.");
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Deduct from sender
            boolean debitSuccess = walletRepository.updateBalance(conn, senderId, -amount);
            if (!debitSuccess) {
                conn.rollback();
                logger.warn("Insufficient funds for user ID: " + senderId);
                return false;
            }

            // Add to receiver
            walletRepository.updateBalance(conn, receiverId, amount);

            // Log Transaction
            Transaction txn = new Transaction();
            txn.setSenderId(senderId);
            txn.setReceiverId(receiverId);
            txn.setAmount(amount);
            txn.setType("SEND");
            txn.setStatus("SUCCESS");
            transactionRepository.logTransaction(conn, txn);

            conn.commit();

            // Notifications
            notificationService.sendNotification(senderId, "Sent " + amount + " to " + receiverIdentifier,
                    "TRANSACTION");
            notificationService.sendNotification(receiverId, "Received " + amount + " from " + senderId, "TRANSACTION");

            // Check Low Balance for Sender
            Optional<com.revpay.model.Wallet> senderWallet = walletRepository.getWalletByUserId(senderId);
            if (senderWallet.isPresent() && senderWallet.get().getBalance() < 500) {
                notificationService.sendNotification(senderId, "Alert: Your balance is below 500!", "ALERT");
            }

            logger.info("Transfer successful: " + amount + " from " + senderId + " to " + receiverId);
            return true;

        } catch (SQLException e) {
            logger.error("Transaction failed: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Rollback failed: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public List<Transaction> getTransactionHistory(int userId) {
        return transactionRepository.getTransactionsByUserId(userId);
    }

    public boolean requestMoney(int senderId, String receiverIdentifier, double amount) {
        Optional<User> receiverOpt = userRepository.findByEmailPhoneOrUsername(receiverIdentifier);
        if (!receiverOpt.isPresent())
            return false;

        int receiverId = receiverOpt.get().getUserId();
        MoneyRequest req = new MoneyRequest();
        req.setSenderId(senderId);
        req.setReceiverId(receiverId);
        req.setAmount(amount);
        req.setStatus("PENDING");

        moneyRequestRepository.createRequest(req);
        notificationService.sendNotification(receiverId, "New Money Request of " + amount + " from user " + senderId,
                "REQUEST");
        return true;
    }

    public List<MoneyRequest> getPendingRequests(int userId) {
        return moneyRequestRepository.getRequestsByReceiverId(userId); // Requests where I am the receiver (payer)
    }

    public boolean processMoneyRequest(int receiverId, int requestId, boolean accept) {
        // receiverId here is the person who received the request (the one who needs to
        // PAY)
        Optional<MoneyRequest> reqOpt = moneyRequestRepository.getRequestById(requestId);
        if (!reqOpt.isPresent())
            return false;

        MoneyRequest req = reqOpt.get();
        if (req.getReceiverId() != receiverId)
            return false; // Not your request
        if (!"PENDING".equalsIgnoreCase(req.getStatus()))
            return false; // Already processed

        if (!accept) {
            moneyRequestRepository.updateStatus(requestId, "DECLINED");
            notificationService.sendNotification(req.getSenderId(),
                    "Request " + requestId + " was declined by user " + receiverId, "REQUEST");
            return true;
        }

        // Accept -> pay
        // We can reuse sendMoney logic but we need to identify user by ID.
        // sendMoney takes identifier string.
        boolean success = sendMoney(receiverId, String.valueOf(req.getSenderId()), req.getAmount());
        if (success) {
            moneyRequestRepository.updateStatus(requestId, "ACCEPTED");
            notificationService.sendNotification(req.getSenderId(),
                    "Request " + requestId + " accepted. Money received.", "TRANSACTION");
            return true;
        }
        return false;
    }
}
