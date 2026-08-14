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
}
