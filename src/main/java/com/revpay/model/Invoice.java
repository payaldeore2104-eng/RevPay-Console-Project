package com.revpay.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Invoice {
    private int invoiceId;
    private int businessId;
    private String customerName;
    private String itemsDescription;
    private double totalAmount;
    private Date dueDate;
    private String status; // PAID, UNPAID
    private Timestamp timestamp;

    public Invoice() {
    }

    public Invoice(int invoiceId, int businessId, String customerName, String itemsDescription, double totalAmount,
            Date dueDate, String status, Timestamp timestamp) {
        this.invoiceId = invoiceId;
        this.businessId = businessId;
        this.customerName = customerName;
        this.itemsDescription = itemsDescription;
        this.totalAmount = totalAmount;
        this.dueDate = dueDate;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getItemsDescription() {
        return itemsDescription;
    }

    public void setItemsDescription(String itemsDescription) {
        this.itemsDescription = itemsDescription;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
