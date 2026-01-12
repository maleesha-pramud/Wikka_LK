package com.wigerlabs.wikka_lk.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wigerlabs.wikka_lk.dto.BrandDTO;
import com.wigerlabs.wikka_lk.entity.Brand;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.regex.Pattern;

public class BrandService {

    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[a-zA-Z\\s'-]{2,20}$"
    );

    // SQL Injection and XSS dangerous patterns
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            ".*(;|--|'|\"|\\bOR\\b|\\bAND\\b|\\bUNION\\b|\\bSELECT\\b|\\bDROP\\b|\\bINSERT\\b|\\bUPDATE\\b|\\bDELETE\\b).*",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern XSS_PATTERN = Pattern.compile(
            ".*(<script|<iframe|javascript:|onerror=|onload=|<object|<embed).*",
            Pattern.CASE_INSENSITIVE
    );

    public String addBrand(BrandDTO brandDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        // Validate name
        if (brandDTO.getName() == null || brandDTO.getName().isBlank()) {
            message = "Brand Name is required.";
        } else if (brandDTO.getName().trim().length() < 2) {
            message = "Brand Name must be at least 2 characters long.";
        } else if (brandDTO.getName().trim().length() > 20) {
            message = "Brand Name must not exceed 20 characters.";
        } else if (!NAME_PATTERN.matcher(brandDTO.getName().trim()).matches()) {
            message = "Brand Name contains invalid characters. Only letters, spaces, hyphens, and apostrophes are allowed.";
        } else if (containsSqlInjection(brandDTO.getName())) {
            message = "Brand Name contains potentially malicious content.";
        } else if (containsXSS(brandDTO.getName())) {
            message = "Brand Name contains potentially malicious scripts.";
        }


        // Validate description (optional field)
        else {
            // All validations passed
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Brand singleBrand = hibernateSession.createNamedQuery("Brand.findByName", Brand.class)
                    .setParameter("name", brandDTO.getName())
                    .getSingleResultOrNull();
            if (singleBrand != null) {
                message = "An brand with this name already exists.";
            } else {
                Brand newBrand = new Brand();
                newBrand.setName(brandDTO.getName().trim());

                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    hibernateSession.persist(newBrand);
                    transaction.commit();
                    status = true;
                    message = "Brand added successfully!";
                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                    }
                    message = "Error occurred while adding brand.";
                } finally {
                    hibernateSession.close();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return responseObject.toString();
    }

    public String updateBrand(BrandDTO brandDTO, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject dataObject = new JsonObject();

        if (brandDTO.getName() == null || brandDTO.getName().isBlank()) {
            message = "Brand Name is required.";
        } else {
            try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                Brand existingBrand = hibernateSession.createNamedQuery("Brand.findById", Brand.class)
                        .setParameter("id", brandDTO.getId())
                        .getSingleResultOrNull();
                if (existingBrand != null) {
                    Transaction transaction = hibernateSession.beginTransaction();
                    try {
                        existingBrand.setName(brandDTO.getName().trim());
                        hibernateSession.merge(existingBrand);
                        transaction.commit();
                        status = true;
                        message = "Brand updated successfully!";
                        dataObject.addProperty("id", existingBrand.getId());
                    } catch (Exception e) {
                        if (transaction != null) {
                            transaction.rollback();
                        }
                        message = "Error occurred while updating brand.";
                    } finally {
                        hibernateSession.close();
                    }
                }
            }
        }
        
        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return responseObject.toString();
    }

    public String getAllBrands() {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonArray dataArray = new JsonArray();

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            List<Brand> brandList = hibernateSession.createQuery("FROM Brand", Brand.class).list();
            for (Brand brand : brandList) {
                JsonObject brandObj = new JsonObject();
                brandObj.addProperty("id", brand.getId());
                brandObj.addProperty("name", brand.getName());
                if (brand.getCreatedAt() != null) {
                    brandObj.addProperty("createdAt", brand.getCreatedAt().toString());
                }
                if (brand.getUpdatedAt() != null) {
                    brandObj.addProperty("updatedAt", brand.getUpdatedAt().toString());
                }
                dataArray.add(brandObj);
            }
            status = true;
            message = "Brands fetched successfully!";
        } catch (Exception e) {
            message = "Error occurred while fetching brands. " + e.getMessage();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);

        return responseObject.toString();
    }

    public String deleteBrand(@Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        String brandIdParam = request.getParameter("id");
        if (brandIdParam == null || brandIdParam.isBlank()) {
            message = "Brand ID is required for deletion.";
        } else {
            try {
                int brandId = Integer.parseInt(brandIdParam);
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Brand existingBrand = hibernateSession.createNamedQuery("Brand.findById", Brand.class)
                            .setParameter("id", brandId)
                            .getSingleResultOrNull();
                    if (existingBrand != null) {
                        Transaction transaction = hibernateSession.beginTransaction();
                        try {
                            hibernateSession.remove(existingBrand);
                            transaction.commit();
                            status = true;
                            message = "Brand deleted successfully!";
                        } catch (Exception e) {
                            if (transaction != null) {
                                transaction.rollback();
                            }
                            message = "Error occurred while deleting brand.";
                        } finally {
                            hibernateSession.close();
                        }
                    } else {
                        message = "Brand not found.";
                    }
                }
            } catch (NumberFormatException e) {
                message = "Invalid Brand ID format.";
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return responseObject.toString();
    }

    /**
     * Check for SQL injection patterns
     */
    private boolean containsSqlInjection(String input) {
        if (input == null) return false;
        return SQL_INJECTION_PATTERN.matcher(input).matches();
    }

    /**
     * Check for XSS attack patterns
     */
    private boolean containsXSS(String input) {
        if (input == null) return false;
        return XSS_PATTERN.matcher(input).matches();
    }
}
