package com.revpay.service;

import com.revpay.model.BusinessDetails;
import com.revpay.model.User;
import com.revpay.repository.BusinessRepository;
import com.revpay.repository.UserRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.util.BCryptUtil;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class AuthService {
    private static final Logger logger = LoggerUtil.getLogger(AuthService.class);
    private final UserRepository userRepository = new UserRepository();
    private final WalletRepository walletRepository = new WalletRepository();
    private final BusinessRepository businessRepository = new BusinessRepository();

    public boolean registerUser(User user, BusinessDetails businessDetails) {

        if (userRepository.findByEmailPhoneOrUsername(user.getEmail()).isPresent() ||
                userRepository.findByEmailPhoneOrUsername(user.getPhone()).isPresent() ||
                (user.getUsername() != null
                        && userRepository.findByEmailPhoneOrUsername(user.getUsername()).isPresent())) {
            logger.warn("User already exists (Email/Phone/Username): " + user.getEmail());
            return false;
        }

        user.setHashedPassword(BCryptUtil.hashPassword(user.getHashedPassword()));

        if (userRepository.registerUser(user)) {
            Optional<User> createdUser = userRepository.findByEmailPhoneOrUsername(user.getEmail());
            if (createdUser.isPresent()) {
                int userId = createdUser.get().getUserId();
                walletRepository.createWallet(userId);

                if ("BUSINESS".equalsIgnoreCase(user.getAccountType()) && businessDetails != null) {
                    businessDetails.setUserId(userId);
                    businessRepository.createBusinessDetails(businessDetails);
                }
                return true;
            }
        }
        return false;
    }

    public User login(String identifier, String password) {
        Optional<User> userOpt = userRepository.findByEmailPhoneOrUsername(identifier);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.isAccountLocked()) {
                logger.warn("Login attempt on locked account: " + identifier);
                throw new RuntimeException("Account is locked due to too many failed attempts.");
            }

            if (BCryptUtil.checkPassword(password, user.getHashedPassword())) {
                if (user.getFailedAttempts() > 0) {
                    userRepository.updateFailedAttempts(user.getUserId(), 0);
                }
                logger.info("User logged in: " + user.getEmail());
                return user;
            } else {
                int attempts = user.getFailedAttempts() + 1;
                userRepository.updateFailedAttempts(user.getUserId(), attempts);
                if (attempts >= 3) {
                    userRepository.lockAccount(user.getUserId());
                    logger.warn("Account locked for user: " + user.getEmail());
                    throw new RuntimeException("Account locked due to too many failed attempts.");
                }
                throw new RuntimeException("Invalid credentials. Attempts remaining: " + (3 - attempts));
            }
        }
        throw new RuntimeException("User not found.");
    }

    // ✅ FIXED resetPassword (Now checks 3 answers)
    public boolean resetPassword(String identifier,
                                 String answer1,
                                 String answer2,
                                 String answer3,
                                 String newPassword) {

        Optional<User> userOpt = userRepository.findByEmailPhoneOrUsername(identifier);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getSecurityAnswer1().equalsIgnoreCase(answer1) &&
                user.getSecurityAnswer2().equalsIgnoreCase(answer2) &&
                user.getSecurityAnswer3().equalsIgnoreCase(answer3)) {

                String hashed = BCryptUtil.hashPassword(newPassword);
                userRepository.updatePassword(user.getUserId(), hashed);

                userRepository.updateFailedAttempts(user.getUserId(), 0);

                if (user.isAccountLocked()) {
                    userRepository.unlockAccount(user.getUserId());
                }

                logger.info("Password reset successful for user: " + user.getEmail());
                return true;
            }
        }
        return false;
    }

    public boolean changePassword(String identifier,
                                  String currentPassword,
                                  String transactionPin,
                                  String newPassword) {

        Optional<User> userOpt = userRepository.findByEmailPhoneOrUsername(identifier);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (!BCryptUtil.checkPassword(currentPassword, user.getHashedPassword())) {
                logger.warn("Password change failed - invalid current password for: " + identifier);
                return false;
            }

            if (!transactionPin.equals(user.getTransactionPin())) {
                logger.warn("Password change failed - invalid PIN for: " + identifier);
                return false;
            }

            String hashed = BCryptUtil.hashPassword(newPassword);
            userRepository.updatePassword(user.getUserId(), hashed);
            logger.info("Password changed successfully for user: " + user.getEmail());
            return true;
        }

        return false;
    }

    // ❌ Removed getSecurityQuestion() method (No longer needed)
}