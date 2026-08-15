package com.wigerlabs.wikka_lk.service;

import com.google.gson.JsonObject;
import com.wigerlabs.wikka_lk.dto.ProductDTO;
import com.wigerlabs.wikka_lk.entity.*;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ProductService {
    public String addProduct(ProductDTO productDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject dataObj = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Model existingModel = hibernateSession.get(Model.class, productDTO.getModelId());
        if (existingModel != null) {
            ProductCondition existingProductCondition = hibernateSession.get(ProductCondition.class, productDTO.getProductConditionId());
            if (existingProductCondition != null) {
                Category existingCategory = hibernateSession.get(Category.class, productDTO.getCategoryId());
                if (existingCategory != null) {
                    User existingUser = hibernateSession.get(User.class, productDTO.getUserId());
                    if (existingUser != null) {
                        Status existingStatus = hibernateSession.get(Status.class, productDTO.getStatusId());
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
                                dataObj.addProperty("id", newProduct.getId());
                            } catch (Exception e) {
                                if (transaction != null) {
                                    transaction.rollback();
                                }
                                message = "Error occurred while adding product. " + e.getMessage();
                            } finally {
                                hibernateSession.close();
                            }
                        } else {
                            message = "Selected Status not found! (ID: " + productDTO.getStatusId() + ")";
                        }
                    } else {
                        message = "Selected User not found! (ID: " + productDTO.getUserId() + ")";
                    }
                } else {
                    message = "Selected Category not found! (ID: " + productDTO.getCategoryId() + ")";
                }
            } else {
                message = "Selected Product Condition not found! (ID: " + productDTO.getProductConditionId() + ")";
            }
        } else {
            message = "Selected Model not found! (ID: " + productDTO.getModelId() + ")";
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        if (status) {
            responseObject.add("data", dataObj);
        }
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
                appendImagesToProductObj(hibernateSession, product, productObj);
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

    public String changeProductStatus(HttpServletRequest req, ProductDTO productDTO) {
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
                            java.util.List<ProductImages> images = hibernateSession.createQuery("FROM ProductImages WHERE product.id = :pid", ProductImages.class)
                                    .setParameter("pid", productId)
                                    .list();
                            
                            for (ProductImages img : images) {
                                String filePath = req.getServletContext().getRealPath("/") + img.getImageUrl();
                                java.io.File file = new java.io.File(filePath);
                                if (file.exists()) {
                                    file.delete();
                                }
                                hibernateSession.remove(img);
                            }

                            hibernateSession.remove(existingProduct);
                            transaction.commit();
                            status = true;
                            message = "Product deleted successfully!";
                        } catch (Exception e) {
                            if (transaction != null) transaction.rollback();
                            
                            Throwable cause = e.getCause();
                            String detail = e.getMessage();
                            while (cause != null) {
                                detail += " -> " + cause.getMessage();
                                cause = cause.getCause();
                            }
                            message = "Error occurred while deleting product. " + detail;
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

    public String getProductsBySeller(jakarta.servlet.http.HttpServletRequest req) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        com.google.gson.JsonArray dataArray = new com.google.gson.JsonArray();

        String userIdParam = req.getParameter("userId");
        if (userIdParam == null || userIdParam.isBlank()) {
            message = "User ID is required!";
        } else {
            try {
                int userId = Integer.parseInt(userIdParam);
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    java.util.List<Product> productList = hibernateSession.createQuery("FROM Product p WHERE p.user.id = :userId", Product.class)
                            .setParameter("userId", userId)
                            .list();
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
                        appendImagesToProductObj(hibernateSession, product, productObj);
                        dataArray.add(productObj);
                    }
                    status = true;
                    message = "Products fetched successfully!";
                }
            } catch (NumberFormatException e) {
                message = "Invalid User ID format.";
            } catch (Exception e) {
                message = "Error occurred while fetching products. " + e.getMessage();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);

        return responseObject.toString();
    }

    private void appendImagesToProductObj(Session session, Product product, JsonObject productObj) {
        java.util.List<ProductImages> imagesList = session.createQuery("FROM ProductImages p WHERE p.product.id = :pid ORDER BY p.position ASC", ProductImages.class)
                .setParameter("pid", product.getId())
                .list();
        com.google.gson.JsonArray imagesArray = new com.google.gson.JsonArray();
        for (ProductImages pi : imagesList) {
            JsonObject imgObj = new JsonObject();
            imgObj.addProperty("id", pi.getId());
            imgObj.addProperty("url", pi.getImageUrl());
            imgObj.addProperty("isPrimary", pi.isPrimary());
            imagesArray.add(imgObj);
        }
        productObj.add("images", imagesArray);
    }

    public String uploadImage(int productId, java.io.InputStream fileInputStream, org.glassfish.jersey.media.multipart.FormDataContentDisposition fileMetaData, jakarta.servlet.http.HttpServletRequest req) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject dataObj = new JsonObject();

        if (fileInputStream == null || fileMetaData == null) {
            message = "File is missing!";
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", message);
            return responseObject.toString();
        }

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Product existingProduct = hibernateSession.get(Product.class, productId);
            if (existingProduct != null) {
                String uploadsDir = req.getServletContext().getRealPath("/") + "uploads/products/";
                java.io.File uploadDirFile = new java.io.File(uploadsDir);
                if (!uploadDirFile.exists()) uploadDirFile.mkdirs();

                String ext = fileMetaData.getFileName().substring(fileMetaData.getFileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID().toString() + ext;
                String filePath = uploadsDir + fileName;
                String dbFilePath = "uploads/products/" + fileName;

                java.nio.file.Files.copy(fileInputStream, java.nio.file.Paths.get(filePath), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    // Temporary schema fix: The product_variant_id column was left over from a previous schema and is NOT NULL by default. 
                    // This alters it to allow NULLs so that new inserts succeed.
                    try {
                        hibernateSession.createNativeMutationQuery("ALTER TABLE product_images MODIFY product_variant_id INT NULL").executeUpdate();
                    } catch (Exception ignored) {
                    }

                    Long count = hibernateSession.createQuery("SELECT count(p) FROM ProductImages p WHERE p.product.id = :pid", Long.class)
                            .setParameter("pid", productId).uniqueResult();

                    ProductImages newImage = new ProductImages();
                    newImage.setProduct(existingProduct);
                    newImage.setImageUrl(dbFilePath);
                    newImage.setPrimary(count == 0);
                    newImage.setPosition(count.intValue());

                    hibernateSession.persist(newImage);
                    transaction.commit();
                    status = true;
                    message = "Image uploaded successfully!";
                    dataObj.addProperty("id", newImage.getId());
                    dataObj.addProperty("url", newImage.getImageUrl());
                    dataObj.addProperty("isPrimary", newImage.isPrimary());
                } catch (Exception e) {
                    if (transaction != null) transaction.rollback();
                    message = "Database error while saving image details: " + e.getMessage();
                }
            } else {
                message = "Product not found!";
            }
        } catch (Exception e) {
            message = "Error uploading image: " + e.getMessage();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataObj);
        return responseObject.toString();
    }

    public String setMainImage(int productId, int imageId) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Product existingProduct = hibernateSession.get(Product.class, productId);
            if (existingProduct != null) {
                ProductImages targetImage = hibernateSession.get(ProductImages.class, imageId);
                if (targetImage != null && targetImage.getProduct().getId() == productId) {
                    Transaction transaction = hibernateSession.beginTransaction();
                    try {
                        hibernateSession.createMutationQuery("UPDATE ProductImages p SET p.isPrimary = false WHERE p.product.id = :pid")
                                .setParameter("pid", productId)
                                .executeUpdate();
                        targetImage.setPrimary(true);
                        hibernateSession.merge(targetImage);
                        transaction.commit();
                        status = true;
                        message = "Main image updated successfully!";
                    } catch (Exception e) {
                        if (transaction != null) transaction.rollback();
                        message = "Error updating main image: " + e.getMessage();
                    }
                } else {
                    message = "Image not found for this product!";
                }
            } else {
                message = "Product not found!";
            }
        } catch (Exception e) {
            message = "Server error: " + e.getMessage();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }

    public String deleteImage(int productId, int imageId, jakarta.servlet.http.HttpServletRequest req) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            ProductImages targetImage = hibernateSession.get(ProductImages.class, imageId);
            if (targetImage != null && targetImage.getProduct().getId() == productId) {
                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    // Try to delete physical file
                    String filePath = req.getServletContext().getRealPath("/") + targetImage.getImageUrl();
                    java.io.File file = new java.io.File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }

                    boolean wasPrimary = targetImage.isPrimary();
                    hibernateSession.remove(targetImage);
                    
                    if (wasPrimary) {
                        // Set another image as primary if exists
                        java.util.List<ProductImages> remaining = hibernateSession.createQuery("FROM ProductImages p WHERE p.product.id = :pid ORDER BY p.position ASC", ProductImages.class)
                                .setParameter("pid", productId)
                                .setMaxResults(1)
                                .list();
                        if (!remaining.isEmpty()) {
                            ProductImages newPrimary = remaining.get(0);
                            newPrimary.setPrimary(true);
                            hibernateSession.merge(newPrimary);
                        }
                    }

                    transaction.commit();
                    status = true;
                    message = "Image deleted successfully!";
                } catch (Exception e) {
                    if (transaction != null) transaction.rollback();
                    message = "Error deleting image: " + e.getMessage();
                }
            } else {
                message = "Image not found for this product!";
            }
        } catch (Exception e) {
            message = "Server error: " + e.getMessage();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }
}
