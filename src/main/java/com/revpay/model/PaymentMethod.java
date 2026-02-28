package com.revpay.model;

public class PaymentMethod {
    private int methodId;
    private int userId;
    private String cardNumberEncrypted;
    private String expiry;
    private String type; // CREDIT, DEBIT
    private boolean isDefault;

    public PaymentMethod() {
    }

    public PaymentMethod(int methodId, int userId, String cardNumberEncrypted, String expiry, String type,
            boolean isDefault) {
        this.methodId = methodId;
        this.userId = userId;
        this.cardNumberEncrypted = cardNumberEncrypted;
        this.expiry = expiry;
        this.type = type;
        this.isDefault = isDefault;
    }

    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCardNumberEncrypted() {
        return cardNumberEncrypted;
    }

    public void setCardNumberEncrypted(String cardNumberEncrypted) {
        this.cardNumberEncrypted = cardNumberEncrypted;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
