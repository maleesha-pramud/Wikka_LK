package com.wigerlabs.wikka_lk.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wigerlabs.wikka_lk.entity.*;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class OrderService {

    public String placeOrder(String jsonData) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject dataObj = new JsonObject();

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            JsonObject reqObj = JsonParser.parseString(jsonData).getAsJsonObject();
            
            if (!reqObj.has("userId") || !reqObj.has("shippingAddress") || !reqObj.has("shippingMethodId") || !reqObj.has("items")) {
                message = "Missing required fields: userId, shippingAddress, shippingMethodId, items";
            } else {
                int userId = reqObj.get("userId").getAsInt();
                String shippingAddress = reqObj.get("shippingAddress").getAsString();
                int shippingMethodId = reqObj.get("shippingMethodId").getAsInt();
                JsonArray itemsArray = reqObj.getAsJsonArray("items");

                User user = hibernateSession.get(User.class, userId);
                ShippingMethod shippingMethod = hibernateSession.get(ShippingMethod.class, shippingMethodId);

                // Auto-create default shipping method if it doesn't exist (e.g. fresh database)
                if (shippingMethod == null && shippingMethodId == 1) {
                    Transaction tx = hibernateSession.beginTransaction();
                    try {
                        shippingMethod = new ShippingMethod();
                        shippingMethod.setName("Standard Delivery");
                        hibernateSession.persist(shippingMethod);
                        tx.commit();
                    } catch (Exception e) {
                        if (tx != null) tx.rollback();
                    }
                }

                if (user == null) {
                    message = "User with ID " + userId + " not found!";
                } else if (shippingMethod == null) {
                    message = "Shipping Method with ID " + shippingMethodId + " not found!";
                } else if (itemsArray.size() == 0) {
                    message = "Cart items array cannot be empty!";
                } else {
                    Transaction transaction = hibernateSession.beginTransaction();
                    try {
                        // 1. Calculate Total Amount
                        double totalAmount = 0.0;
                        for (JsonElement element : itemsArray) {
                            JsonObject itemObj = element.getAsJsonObject();
                            int productId = itemObj.get("productId").getAsInt();
                            int qty = itemObj.get("qty").getAsInt();
                            Product product = hibernateSession.get(Product.class, productId);
                            if (product != null) {
                                totalAmount += (product.getBasePrice() * qty);
                            }
                        }

                        if (totalAmount > 0) {
                            // 2. Create Payment Details (Pending)
                            Status pendingStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                                    .setParameter("value", Status.Type.PENDING.getValue()).getSingleResultOrNull();

                            PaymentDetails paymentDetails = new PaymentDetails();
                            paymentDetails.setPaymentMethod("PayHere");
                            paymentDetails.setTransactionId("PENDING");
                            paymentDetails.setStatus(pendingStatus);
                            hibernateSession.persist(paymentDetails);

                            // 3. Create Order
                            Order order = new Order();
                            order.setUser(user);
                            order.setShippingAddress(shippingAddress);
                            order.setShippingMethod(shippingMethod);
                            order.setPaymentDetails(paymentDetails);
                            order.setStatus(pendingStatus);
                            order.setTotalAmount(totalAmount);
                            hibernateSession.persist(order);

                            // 4. Create Order Items
                            for (JsonElement element : itemsArray) {
                                JsonObject itemObj = element.getAsJsonObject();
                                int productId = itemObj.get("productId").getAsInt();
                                int qty = itemObj.get("qty").getAsInt();
                                Product product = hibernateSession.get(Product.class, productId);
                                
                                if (product != null) {
                                    OrderItem orderItem = new OrderItem();
                                    orderItem.setOrder(order);
                                    orderItem.setProduct(product);
                                    orderItem.setQty(qty);
                                    orderItem.setUnitPrice(product.getBasePrice());
                                    hibernateSession.persist(orderItem);
                                }
                            }

                            transaction.commit();
                            status = true;
                            message = "Order placed successfully!";
                            dataObj.addProperty("orderId", order.getId());
                        } else {
                            message = "Invalid products or zero total amount.";
                        }
                    } catch (Exception e) {
                        if (transaction != null) transaction.rollback();
                        message = "Error saving order: " + e.getMessage();
                    }
                }
            }
        } catch (Exception e) {
            message = "Server error: " + e.getMessage();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        if (status) responseObject.add("data", dataObj);

        return responseObject.toString();
    }

    public String getOrdersByBuyer(HttpServletRequest req) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonArray dataArray = new JsonArray();

        String userIdParam = req.getParameter("userId");
        if (userIdParam == null || userIdParam.isBlank()) {
            message = "User ID is required!";
        } else {
            try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                int userId = Integer.parseInt(userIdParam);
                List<Order> orders = hibernateSession.createQuery("FROM Order o WHERE o.user.id = :uid ORDER BY o.id DESC", Order.class)
                        .setParameter("uid", userId)
                        .list();

                for (Order order : orders) {
                    JsonObject orderObj = new JsonObject();
                    orderObj.addProperty("id", order.getId());
                    orderObj.addProperty("totalAmount", order.getTotalAmount());
                    orderObj.addProperty("shippingAddress", order.getShippingAddress());
                    orderObj.addProperty("status", order.getStatus().getValue());
                    orderObj.addProperty("paymentMethod", order.getPaymentDetails().getPaymentMethod());
                    orderObj.addProperty("transactionId", order.getPaymentDetails().getTransactionId());
                    
                    if (order.getCreatedAt() != null) {
                        orderObj.addProperty("createdAt", order.getCreatedAt().toString());
                    }

                    // Fetch items
                    List<OrderItem> items = hibernateSession.createQuery("FROM OrderItem oi WHERE oi.order.id = :oid", OrderItem.class)
                            .setParameter("oid", order.getId())
                            .list();
                            
                    JsonArray itemsArr = new JsonArray();
                    for (OrderItem item : items) {
                        JsonObject itemObj = new JsonObject();
                        itemObj.addProperty("productId", item.getProduct().getId());
                        itemObj.addProperty("productName", item.getProduct().getName());
                        itemObj.addProperty("qty", item.getQty());
                        itemObj.addProperty("unitPrice", item.getUnitPrice());
                        
                        // Try to append main image
                        try {
                            List<ProductImages> images = hibernateSession.createQuery("FROM ProductImages i WHERE i.product.id = :pid AND i.isMain = true", ProductImages.class)
                                .setParameter("pid", item.getProduct().getId())
                                .list();
                            if (!images.isEmpty()) {
                                itemObj.addProperty("mainImage", images.get(0).getImageUrl());
                            }
                        } catch (Exception ignored) {}
                        
                        itemsArr.add(itemObj);
                    }
                    orderObj.add("items", itemsArr);
                    dataArray.add(orderObj);
                }

                status = true;
                message = "Orders fetched successfully!";
            } catch (Exception e) {
                message = "Server error: " + e.getMessage();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        if (status) responseObject.add("data", dataArray);

        return responseObject.toString();
    }
}
