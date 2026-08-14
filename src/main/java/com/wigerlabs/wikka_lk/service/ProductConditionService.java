package com.wigerlabs.wikka_lk.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wigerlabs.wikka_lk.entity.ProductCondition;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class ProductConditionService {

    public String getAllProductConditions() {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonArray dataArray = new JsonArray();

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            List<ProductCondition> conditionList = hibernateSession.createQuery("FROM ProductCondition", ProductCondition.class).list();
            for (ProductCondition condition : conditionList) {
                JsonObject conditionObj = new JsonObject();
                conditionObj.addProperty("id", condition.getId());
                conditionObj.addProperty("name", condition.getName());
                dataArray.add(conditionObj);
            }
            status = true;
            message = "Product conditions fetched successfully!";
        } catch (Exception e) {
            message = "Error occurred while fetching product conditions. " + e.getMessage();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);

        return responseObject.toString();
    }

    public String addProductCondition(com.wigerlabs.wikka_lk.dto.ProductConditionDTO conditionDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        ProductCondition existingCondition = hibernateSession.createQuery("FROM ProductCondition p WHERE p.name = :name", ProductCondition.class)
                .setParameter("name", conditionDTO.getName())
                .getSingleResultOrNull();
        if (existingCondition != null) {
            message = "A product condition with this name already exists.";
        } else {
            ProductCondition newCondition = new ProductCondition();
            newCondition.setName(conditionDTO.getName().trim());

            org.hibernate.Transaction transaction = hibernateSession.beginTransaction();
            try {
                hibernateSession.persist(newCondition);
                transaction.commit();
                status = true;
                message = "Product condition added successfully!";
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                message = "Error occurred while adding product condition.";
            } finally {
                hibernateSession.close();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }

    public String updateProductCondition(jakarta.servlet.http.HttpServletRequest req, com.wigerlabs.wikka_lk.dto.ProductConditionDTO conditionDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            message = "Product Condition ID is required!";
        } else {
            conditionDTO.setId(Integer.parseInt(idParam));
            try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                ProductCondition existingCondition = hibernateSession.createNamedQuery("ProductCondition.findById", ProductCondition.class)
                        .setParameter("id", conditionDTO.getId())
                        .getSingleResultOrNull();
                if (existingCondition != null) {
                    org.hibernate.Transaction transaction = hibernateSession.beginTransaction();
                    try {
                        existingCondition.setName(conditionDTO.getName().trim());
                        hibernateSession.merge(existingCondition);
                        transaction.commit();
                        status = true;
                        message = "Product condition updated successfully!";
                    } catch (Exception e) {
                        if (transaction != null) {
                            transaction.rollback();
                        }
                        message = "Error occurred while updating product condition.";
                    }
                } else {
                    message = "Product condition not found!";
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }

    public String deleteProductCondition(jakarta.servlet.http.HttpServletRequest req) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            message = "Product Condition ID is required for deletion.";
        } else {
            try {
                int conditionId = Integer.parseInt(idParam);
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    ProductCondition existingCondition = hibernateSession.find(ProductCondition.class, conditionId);
                    if (existingCondition != null) {
                        org.hibernate.Transaction transaction = hibernateSession.beginTransaction();
                        try {
                            hibernateSession.remove(existingCondition);
                            transaction.commit();
                            status = true;
                            message = "Product condition deleted successfully!";
                        } catch (Exception e) {
                            if (transaction != null) {
                                transaction.rollback();
                            }
                            message = "Error occurred while deleting product condition.";
                        }
                    } else {
                        message = "Product condition not found.";
                    }
                }
            } catch (NumberFormatException e) {
                message = "Invalid Product Condition ID format.";
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }
}

