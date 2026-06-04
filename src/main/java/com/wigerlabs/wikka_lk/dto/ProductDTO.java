package com.wigerlabs.wikka_lk.dto;

import java.io.Serializable;

public class ProductDTO implements Serializable {
    private int id;
    private String name;
    private double basePrice;
    private String description;
    private String contactNo;
    private int modelId;
    private int productConditionId;
    private int categoryId;
    private int userId;
    private int statusId;

    public ProductDTO() {
    }

    public ProductDTO(int id, String name, double basePrice, String description, String contactNo, int modelId, int productConditionId, int categoryId, int userId, int statusId) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
        this.contactNo = contactNo;
        this.modelId = modelId;
        this.productConditionId = productConditionId;
        this.categoryId = categoryId;
        this.userId = userId;
        this.statusId = statusId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getProductConditionId() {
        return productConditionId;
    }

    public void setProductConditionId(int productConditionId) {
        this.productConditionId = productConditionId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
