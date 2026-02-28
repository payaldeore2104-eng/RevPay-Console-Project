package com.revpay.service;

import com.revpay.model.PaymentMethod;
import com.revpay.repository.PaymentMethodRepository;
import com.revpay.util.AESUtil;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PaymentService {
    private static final Logger logger = LoggerUtil.getLogger(PaymentService.class);
    private final PaymentMethodRepository paymentMethodRepository = new PaymentMethodRepository();

    public void addCard(int userId, String cardNumber, String expiry, String type) {
        try {
            String encryptedCard = AESUtil.encrypt(cardNumber);
            PaymentMethod method = new PaymentMethod();
            method.setUserId(userId);
            method.setCardNumberEncrypted(encryptedCard);
            method.setExpiry(expiry);
            method.setType(type);
            method.setDefault(false); // Logic to set first card as default could be added

            List<PaymentMethod> existing = paymentMethodRepository.getMethodsByUserId(userId);
            if (existing.isEmpty()) {
                method.setDefault(true);
            } else if (method.isDefault()) {
                paymentMethodRepository.resetDefault(userId);
            }

            paymentMethodRepository.addPaymentMethod(method);
        } catch (Exception e) {
            logger.error("Error encrypting card: " + e.getMessage());
            throw new RuntimeException("Error adding card.");
        }
    }

    public List<PaymentMethod> getUserCards(int userId) {
        return paymentMethodRepository.getMethodsByUserId(userId);
    }

    public void removeCard(int methodId) {
        paymentMethodRepository.deletePaymentMethod(methodId);
    }
}
