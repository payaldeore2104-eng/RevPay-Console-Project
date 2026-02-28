package com.revpay.model;

public class BusinessDetails {
    private int businessId;
    private int userId;
    private String businessName;
    private String businessType;
    private String taxId;
    private String address;
    private String verificationDocumentPath;

    public BusinessDetails() {
    }

    public BusinessDetails(int businessId, int userId, String businessName, String businessType, String taxId,
            String address, String verificationDocumentPath) {
        this.businessId = businessId;
        this.userId = userId;
        this.businessName = businessName;
        this.businessType = businessType;
        this.taxId = taxId;
        this.address = address;
        this.verificationDocumentPath = verificationDocumentPath;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVerificationDocumentPath() {
        return verificationDocumentPath;
    }

    public void setVerificationDocumentPath(String verificationDocumentPath) {
        this.verificationDocumentPath = verificationDocumentPath;
    }
}
