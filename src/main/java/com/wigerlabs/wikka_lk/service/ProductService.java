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

    public String getAllProducts() {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        com.google.gson.JsonArray dataArray = new com.google.gson.JsonArray();

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            java.util.List<Product> productList = hibernateSession.createQuery("FROM Product", Product.class).list();
            for (Product product : productList) {
                JsonObject productObj = new JsonObject();
                productObj.addProperty("id", product.getId());
                productObj.addProperty("name", product.getName());
                productObj.addProperty("basePrice", product.getBasePrice());
                productObj.addProperty("description", product.getDescription());
                productObj.addProperty("contactNo", product.getContactNo());
                productObj.addProperty("modelId", product.getModel().getId());
                productObj.addProperty("modelName", product.getModel().getName());
                productObj.addProperty("productConditionId", product.getProductCondition().getId());
                productObj.addProperty("productConditionName", product.getProductCondition().getName());
                productObj.addProperty("categoryId", product.getCategory().getId());
                productObj.addProperty("categoryName", product.getCategory().getName());
                productObj.addProperty("userId", product.getUser().getId());
                productObj.addProperty("userName", product.getUser().getName());
                productObj.addProperty("statusId", product.getStatus().getId());
                productObj.addProperty("statusValue", product.getStatus().getValue());
                if (product.getCreatedAt() != null) {
                    productObj.addProperty("createdAt", product.getCreatedAt().toString());
                }
                dataArray.add(productObj);
            }
            status = true;
            message = "Products fetched successfully!";
        } catch (Exception e) {
            message = "Error occurred while fetching products. " + e.getMessage();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);

        return responseObject.toString();
    }

    public String changeProductStatus(ProductDTO productDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (productDTO.getId() <= 0) {
            message = "Valid Product ID is required.";
        } else if (productDTO.getStatusId() <= 0) {
            message = "Valid Status ID is required.";
        } else {
            try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                Product product = hibernateSession.get(Product.class, productDTO.getId());
                if (product == null) {
                    message = "Product not found.";
                } else {
                    Status newStatus = hibernateSession.createNamedQuery("Status.findById", Status.class)
                            .setParameter("id", productDTO.getStatusId())
                            .getSingleResultOrNull();

                    if (newStatus == null) {
                        message = "Invalid status specified.";
                    } else {
                        Transaction transaction = hibernateSession.beginTransaction();
                        try {
                            product.setStatus(newStatus);
                            hibernateSession.merge(product);
                            transaction.commit();
                            status = true;
                            message = "Product status updated successfully.";
                        } catch (Exception e) {
                            if (transaction != null) {
                                transaction.rollback();
                            }
                            message = "Error occurred while updating product status.";
                        }
                    }
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }

    public String viewProduct(jakarta.servlet.http.HttpServletRequest req) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject productObj = new JsonObject();

        String productIdParam = req.getParameter("id");
        if (productIdParam == null || productIdParam.isBlank()) {
            message = "Product ID is required!";
        } else {
            try {
                int productId = Integer.parseInt(productIdParam);
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Product product = hibernateSession.get(Product.class, productId);
                    if (product != null) {
                        productObj.addProperty("id", product.getId());
                        productObj.addProperty("name", product.getName());
                        productObj.addProperty("basePrice", product.getBasePrice());
                        productObj.addProperty("description", product.getDescription());
                        productObj.addProperty("contactNo", product.getContactNo());
                        productObj.addProperty("modelId", product.getModel().getId());
                        productObj.addProperty("modelName", product.getModel().getName());
                        productObj.addProperty("productConditionId", product.getProductCondition().getId());
                        productObj.addProperty("productConditionName", product.getProductCondition().getName());
                        productObj.addProperty("categoryId", product.getCategory().getId());
                        productObj.addProperty("categoryName", product.getCategory().getName());
                        productObj.addProperty("userId", product.getUser().getId());
                        productObj.addProperty("userName", product.getUser().getName());
                        productObj.addProperty("statusId", product.getStatus().getId());
                        productObj.addProperty("statusValue", product.getStatus().getValue());
                        if (product.getCreatedAt() != null) {
                            productObj.addProperty("createdAt", product.getCreatedAt().toString());
                        }
                        status = true;
                        message = "Product fetched successfully!";
                        responseObject.add("data", productObj);
                    } else {
                        message = "Product not found.";
                    }
                }
            } catch (NumberFormatException e) {
                message = "Invalid Product ID format.";
            }
        }
        
        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }

    public String updateProduct(jakarta.servlet.http.HttpServletRequest req, ProductDTO productDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        String productIdParam = req.getParameter("id");
        if (productIdParam == null || productIdParam.isBlank()) {
            message = "Product ID is required!";
        } else {
            try {
                int productId = Integer.parseInt(productIdParam);
                productDTO.setId(productId);
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Product existingProduct = hibernateSession.get(Product.class, productId);
                    if (existingProduct != null) {
                        Model existingModel = hibernateSession.get(Model.class, productDTO.getModelId());
                        ProductCondition existingCondition = hibernateSession.get(ProductCondition.class, productDTO.getProductConditionId());
                        Category existingCategory = hibernateSession.get(Category.class, productDTO.getCategoryId());
                        User existingUser = hibernateSession.get(User.class, productDTO.getUserId());
                        Status existingStatus = hibernateSession.get(Status.class, productDTO.getStatusId());

                        if (existingModel != null && existingCondition != null && existingCategory != null && existingUser != null && existingStatus != null) {
                            Transaction transaction = hibernateSession.beginTransaction();
                            try {
                                existingProduct.setName(productDTO.getName());
                                existingProduct.setBasePrice(productDTO.getBasePrice());
                                existingProduct.setDescription(productDTO.getDescription());
                                existingProduct.setContactNo(productDTO.getContactNo());
                                existingProduct.setModel(existingModel);
                                existingProduct.setProductCondition(existingCondition);
                                existingProduct.setCategory(existingCategory);
                                existingProduct.setUser(existingUser);
                                existingProduct.setStatus(existingStatus);

                                hibernateSession.merge(existingProduct);
                                transaction.commit();
                                status = true;
                                message = "Product updated successfully!";
                            } catch (Exception e) {
                                if (transaction != null) transaction.rollback();
                                message = "Error occurred while updating product. " + e.getMessage();
                            }
                        } else {
                            message = "One or more related entities not found.";
                        }
                    } else {
                        message = "Product not found.";
                    }
                }
            } catch (NumberFormatException e) {
                message = "Invalid Product ID format.";
            }
        }
        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }

    public String deleteProduct(jakarta.servlet.http.HttpServletRequest req) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        String productIdParam = req.getParameter("id");
        if (productIdParam == null || productIdParam.isBlank()) {
            message = "Product ID is required for deletion!";
        } else {
            try {
                int productId = Integer.parseInt(productIdParam);
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Product existingProduct = hibernateSession.get(Product.class, productId);
                    if (existingProduct != null) {
                        Transaction transaction = hibernateSession.beginTransaction();
                        try {
                            hibernateSession.remove(existingProduct);
                            transaction.commit();
                            status = true;
                            message = "Product deleted successfully!";
                        } catch (Exception e) {
                            if (transaction != null) transaction.rollback();
                            message = "Error occurred while deleting product. " + e.getMessage();
                        }
                    } else {
                        message = "Product not found.";
                    }
                }
            } catch (NumberFormatException e) {
                message = "Invalid Product ID format.";
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }
}
