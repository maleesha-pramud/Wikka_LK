package com.wigerlabs.wikka_lk.service;

import com.google.gson.JsonObject;
import com.wigerlabs.wikka_lk.dto.ProductDTO;
import com.wigerlabs.wikka_lk.entity.*;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ProductService {
    public String addProduct(ProductDTO productDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Model existingModel = hibernateSession.createNamedQuery("Model.findById", Model.class)
                .setParameter("id", productDTO.getModelId())
                .getSingleResultOrNull();
        if (existingModel != null) {
            ProductCondition existingProductCondition = hibernateSession.createNamedQuery("ProductCondition.findById", ProductCondition.class)
                    .setParameter("id", productDTO.getProductConditionId())
                    .getSingleResultOrNull();
            if (existingProductCondition != null) {
                Category existingCategory = hibernateSession.createNamedQuery("Category.findById", Category.class)
                        .setParameter("id", productDTO.getCategoryId())
                        .getSingleResultOrNull();
                if (existingCategory != null) {
                    User existingUser = hibernateSession.createNamedQuery("User.findById", User.class)
                            .setParameter("id", productDTO.getUserId())
                            .getSingleResultOrNull();
                    if (existingUser != null) {
                        Status existingStatus = hibernateSession.createNamedQuery("Status.findById", Status.class)
                                .setParameter("id", productDTO.getStatusId())
                                .getSingleResultOrNull();
                        if (existingStatus != null) {
                            Product newProduct = new Product();
                            newProduct.setName(productDTO.getName());
                            newProduct.setBasePrice(productDTO.getBasePrice());
                            newProduct.setDescription(productDTO.getDescription());
                            newProduct.setContactNo(productDTO.getContactNo());
                            newProduct.setModel(existingModel);
                            newProduct.setProductCondition(existingProductCondition);
                            newProduct.setCategory(existingCategory);
                            newProduct.setUser(existingUser);
                            newProduct.setStatus(existingStatus);

                            Transaction transaction = hibernateSession.beginTransaction();
                            try {
                                hibernateSession.persist(newProduct);
                                transaction.commit();
                                status = true;
                                message = "Product added successfully!";
                            } catch (Exception e) {
                                if (transaction != null) {
                                    transaction.rollback();
                                }
                                message = "Error occurred while adding product.";
                            } finally {
                                hibernateSession.close();
                            }
                        } else {
                            message = "Selected Status not found!";
                        }
                    } else {
                        message = "Selected User not found!";
                    }
                } else {
                    message = "Selected Category not found!";
                }
            } else {
                message = "Selected Product Condition not found!";
            }
        } else {
            message = "Selected Model not found!";
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }
}
