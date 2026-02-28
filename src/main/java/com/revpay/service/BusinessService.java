package com.revpay.service;

import com.revpay.model.BusinessDetails;
import com.revpay.model.Invoice;
import com.revpay.repository.BusinessRepository;
import com.revpay.repository.InvoiceRepository;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public class BusinessService {
    private static final Logger logger = LoggerUtil.getLogger(BusinessService.class);
    private final BusinessRepository businessRepository = new BusinessRepository();
    private final InvoiceRepository invoiceRepository = new InvoiceRepository();

    public void createInvoice(int userId, String customerName, String items, double amount, Date dueDate) {
        Optional<BusinessDetails> businessOpt = businessRepository.getBusinessByUserId(userId);
        if (businessOpt.isPresent()) {
            Invoice invoice = new Invoice();
            invoice.setBusinessId(businessOpt.get().getBusinessId());
            invoice.setCustomerName(customerName);
            invoice.setItemsDescription(items);
            invoice.setTotalAmount(amount);
            invoice.setDueDate(dueDate);
            invoice.setStatus("UNPAID");

            invoiceRepository.createInvoice(invoice);
            logger.info(
                    "Invoice created for business ID: " + businessOpt.get().getBusinessId() + ", Amount: " + amount);
        } else {
            logger.warn("No business account found for user: " + userId);
            throw new RuntimeException("User is not a business account holder.");
        }
    }

    public List<Invoice> getBusinessInvoices(int userId) {
        Optional<BusinessDetails> businessOpt = businessRepository.getBusinessByUserId(userId);
        if (businessOpt.isPresent()) {
            return invoiceRepository.getInvoicesByBusinessId(businessOpt.get().getBusinessId());
        }
        return java.util.Collections.emptyList();
    }

    public void markInvoicePaid(int invoiceId) {
        invoiceRepository.updateStatus(invoiceId, "PAID");
    }

    // Analytics: Simple aggregation for now
    public String getBusinessAnalytics(int userId) {
        Optional<BusinessDetails> businessOpt = businessRepository.getBusinessByUserId(userId);
        if (!businessOpt.isPresent())
            return "No business found.";

        List<Invoice> invoices = invoiceRepository.getInvoicesByBusinessId(businessOpt.get().getBusinessId());

        StringBuilder report = new StringBuilder();
        report.append(String.format("=== Business Analytics for User %d ===\n", userId));

        // 1. Total Revenue
        double totalRevenue = invoices.stream()
                .filter(i -> "PAID".equals(i.getStatus()))
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
        report.append(String.format("Total Revenue: $%.2f\n", totalRevenue));

        // 2. Pending Invoices List
        long pendingCount = invoices.stream().filter(i -> "UNPAID".equals(i.getStatus())).count();
        report.append(String.format("Pending Invoices: %d\n", pendingCount));

        // 3. Monthly Transactions (Group by Month)
        // Note: Invoices are not exactly "transactions" but represent sales.
        // We can also look at actual money requests or wallet transactions if we had
        // access to TransactionService here.
        // For compliance with "Monthly transactions (group by month)", we will filter
        // Invoices by month.
        java.util.Map<String, Double> monthlyRevenue = invoices.stream()
                .filter(i -> "PAID".equals(i.getStatus()))
                .collect(java.util.stream.Collectors.groupingBy(
                        i -> new java.text.SimpleDateFormat("yyyy-MM").format(i.getTimestamp()),
                        java.util.stream.Collectors.summingDouble(Invoice::getTotalAmount)));

        report.append("\nRevenue by Month:\n");
        monthlyRevenue.forEach((k, v) -> report.append(String.format("  %s: $%.2f\n", k, v)));

        // 4. Top Customers
        report.append("\nTop Customers:\n");
        java.util.Map<String, Double> customerSales = invoices.stream()
                .filter(i -> "PAID".equals(i.getStatus()))
                .collect(java.util.stream.Collectors.groupingBy(
                        Invoice::getCustomerName,
                        java.util.stream.Collectors.summingDouble(Invoice::getTotalAmount)));

        customerSales.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .forEach(e -> report.append(String.format("  %s: $%.2f\n", e.getKey(), e.getValue())));

        return report.toString();
    }
}
