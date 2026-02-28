package com.revpay.service;

import com.revpay.model.Wallet;
import com.revpay.model.Transaction;
import com.revpay.repository.TransactionRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class WalletService {
    private static final Logger logger = LoggerUtil.getLogger(WalletService.class);
    private final WalletRepository walletRepository = new WalletRepository();
    private final NotificationService notificationService = new NotificationService();

    public double getBalance(int userId) {
        Optional<Wallet> wallet = walletRepository.getWalletByUserId(userId);
        return wallet.map(Wallet::getBalance).orElse(0.0);
    }

    public boolean addMoney(int userId, double amount) {
        if (amount <= 0)
            return false;

        boolean success = walletRepository.updateBalance(userId, amount);

        if (success) {
            Transaction txn = new Transaction();
            txn.setSenderId(0); // system deposit
            txn.setReceiverId(userId);
            txn.setAmount(amount);
            txn.setType("ADD_MONEY");
            txn.setStatus("SUCCESS");

            TransactionRepository repo = new TransactionRepository();
            repo.logTransaction(txn);
        }

        return success;
    }

    public boolean withdrawMoney(int userId, double amount) {
        if (amount <= 0)
            return false;

        double balance = getBalance(userId);
        if (balance >= amount) {

            boolean success = walletRepository.updateBalance(userId, -amount);

            if (success) {

                // 🔹 Log transaction
                Transaction txn = new Transaction();
                txn.setSenderId(userId);
                txn.setReceiverId(0); // system withdrawal
                txn.setAmount(amount);
                txn.setType("WITHDRAW");
                txn.setStatus("SUCCESS");

                TransactionRepository repo = new TransactionRepository();
                repo.logTransaction(txn);

                double newBalance = getBalance(userId);
                if (newBalance < 500) {
                    notificationService.sendNotification(userId,
                            "Alert: Your balance is below 500!", "ALERT");
                    logger.warn("Low balance alert triggered for user "
                            + userId + ". Balance: " + newBalance);
                }
            }

            return success;
        }

        return false;
    }
}