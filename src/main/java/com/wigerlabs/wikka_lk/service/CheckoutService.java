package com.wigerlabs.wikka_lk.service;

import com.google.gson.JsonObject;
import com.wigerlabs.wikka_lk.entity.Order;
import com.wigerlabs.wikka_lk.entity.PaymentDetails;
import com.wigerlabs.wikka_lk.entity.Status;
import com.wigerlabs.wikka_lk.util.AppUtil;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.text.DecimalFormat;

public class CheckoutService {

    // Configure PayHere details here (Sandbox testing default values)
    public static final String MERCHANT_ID = "1227844"; // Replace with your merchant ID
    public static final String MERCHANT_SECRET = "MzI2MTAyMzMwODExMjkxMDI3OTU0MTE5MDQ5NjE5MTMzNzgxNjAzMg=="; // Replace with your merchant secret
    public static final String CURRENCY = AppUtil.MAIN_APP_CURRENCY;

    public String generateHash(String orderIdParam) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject dataObj = new JsonObject();

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Order order = null;
            try {
                int parsedId = Integer.parseInt(orderIdParam);
                order = hibernateSession.get(Order.class, parsedId);
            } catch (NumberFormatException ignored) {
                // Not an integer ID, might be a mock string from frontend like "ORD-178..."
            }

            double amount = 0.0;
            boolean validOrderFound = false;

            if (order != null) {
                amount = order.getTotalAmount();
                validOrderFound = true;
            } else if (orderIdParam.startsWith("ORD-")) {
                // Mock test fallback so the frontend developer can test the checkout window
                amount = 1500.00;
                validOrderFound = true;
                System.out.println("PayHere Warning: Using mock amount (1500.00) for testing since real order was not found for ID: " + orderIdParam);
            }

            if (validOrderFound) {
                DecimalFormat df = new DecimalFormat("0.00");
                String amountFormatted = df.format(amount);

                String hashedSecret = AppUtil.getMd5(MERCHANT_SECRET).toUpperCase();
                String hashInput = MERCHANT_ID + orderIdParam + amountFormatted + CURRENCY + hashedSecret;
                String generatedHash = AppUtil.getMd5(hashInput).toUpperCase();

                dataObj.addProperty("merchantId", MERCHANT_ID);
                dataObj.addProperty("hash", generatedHash);
                dataObj.addProperty("amount", amountFormatted);
                dataObj.addProperty("currency", CURRENCY);
                dataObj.addProperty("orderId", orderIdParam);

                status = true;
                message = "Hash generated successfully";
            } else {
                message = "Order not found! Please pass a valid integer order ID.";
            }
        } catch (Exception e) {
            message = "Server error: " + e.getMessage();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        if (status) {
            responseObject.add("data", dataObj);
        }

        return responseObject.toString();
    }

    public void processNotify(String orderIdParam, String transactionId, String statusCode, String receivedHash, String payhereAmount, String payhereCurrency) {
        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            int orderId = Integer.parseInt(orderIdParam);
            Order order = hibernateSession.get(Order.class, orderId);

            if (order != null) {
                // Verify Hash securely
                String hashedSecret = AppUtil.getMd5(MERCHANT_SECRET).toUpperCase();
                String hashInput = MERCHANT_ID + orderIdParam + payhereAmount + payhereCurrency + hashedSecret;
                String localHash = AppUtil.getMd5(hashInput).toUpperCase();

                if (localHash.equals(receivedHash)) {
                    Transaction transaction = hibernateSession.beginTransaction();
                    try {
                        PaymentDetails payment = order.getPaymentDetails();
                        if (payment != null) {
                            payment.setTransactionId(transactionId != null ? transactionId : "UNKNOWN");
                            
                            // Payhere Status Codes: 2 = Success, 0 = Pending, -1 = Canceled, -2 = Failed, -3 = Charged back
                            Status newStatus = null;
                            if ("2".equals(statusCode)) {
                                newStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                                        .setParameter("value", Status.Type.COMPLETED.getValue()).getSingleResultOrNull();
                            } else if ("-1".equals(statusCode) || "-2".equals(statusCode) || "-3".equals(statusCode)) {
                                newStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                                        .setParameter("value", Status.Type.CANCELED.getValue()).getSingleResultOrNull();
                            }
                            
                            if (newStatus != null) {
                                payment.setStatus(newStatus);
                                order.setStatus(newStatus);
                            }
                            
                            hibernateSession.merge(payment);
                            hibernateSession.merge(order);
                        }
                        transaction.commit();
                        System.out.println("PayHere Webhook: Successfully processed payment for Order ID " + orderId);
                    } catch (Exception e) {
                        if (transaction != null) transaction.rollback();
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("PayHere Webhook: Hash verification failed for Order ID " + orderIdParam);
                }
            } else {
                System.err.println("PayHere Webhook: Order not found for ID " + orderIdParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
