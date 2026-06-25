package com.wigerlabs.wikka_lk.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wigerlabs.wikka_lk.dto.ColorDTO;
import com.wigerlabs.wikka_lk.entity.Color;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.regex.Pattern;

public class ColorService {

    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[a-zA-Z\\s'-]{2,20}$"
    );
    
    private static final Pattern HEX_CODE_PATTERN = Pattern.compile(
            "^#([A-Fa-f0-9]{6})$"
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

    public String addColor(ColorDTO colorDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        // Validate name
        if (colorDTO.getName() == null || colorDTO.getName().isBlank()) {
            message = "Color Name is required.";
        } else if (colorDTO.getName().trim().length() < 2) {
            message = "Color Name must be at least 2 characters long.";
        } else if (colorDTO.getName().trim().length() > 20) {
            message = "Color Name must not exceed 20 characters.";
        } else if (!NAME_PATTERN.matcher(colorDTO.getName().trim()).matches()) {
            message = "Color Name contains invalid characters. Only letters, spaces, hyphens, and apostrophes are allowed.";
        } else if (containsSqlInjection(colorDTO.getName())) {
            message = "Color Name contains potentially malicious content.";
        } else if (containsXSS(colorDTO.getName())) {
            message = "Color Name contains potentially malicious scripts.";
        } else if (colorDTO.getHexCode() == null || colorDTO.getHexCode().isBlank()) {
            message = "Hex Code is required.";
        } else if (!HEX_CODE_PATTERN.matcher(colorDTO.getHexCode().trim()).matches()) {
            message = "Invalid Hex Code format. It should start with # followed by 6 hex characters.";
        }
        else {
            // All validations passed
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Color singleColor = hibernateSession.createNamedQuery("Color.findByName", Color.class)
                    .setParameter("name", colorDTO.getName())
                    .getSingleResultOrNull();
            if (singleColor != null) {
                message = "A color with this name already exists.";
            } else {
                Color newColor = new Color();
                newColor.setName(colorDTO.getName().trim());
                newColor.setHexCode(colorDTO.getHexCode().trim());

                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    hibernateSession.persist(newColor);
                    transaction.commit();
                    status = true;
                    message = "Color added successfully!";
                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                    }
                    message = "Error occurred while adding color. " + e.getMessage();
                } finally {
                    hibernateSession.close();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return responseObject.toString();
    }

    public String getAllColors() {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonArray dataArray = new JsonArray();

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            List<Color> colorList = hibernateSession.createQuery("FROM Color", Color.class).list();
            for (Color color : colorList) {
                JsonObject colorObj = new JsonObject();
                colorObj.addProperty("id", color.getId());
                colorObj.addProperty("name", color.getName());
                colorObj.addProperty("hexCode", color.getHexCode());
                if (color.getCreatedAt() != null) {
                    colorObj.addProperty("createdAt", color.getCreatedAt().toString());
                }
                if (color.getUpdatedAt() != null) {
                    colorObj.addProperty("updatedAt", color.getUpdatedAt().toString());
                }
                dataArray.add(colorObj);
            }
            status = true;
            message = "Colors fetched successfully!";
        } catch (Exception e) {
            message = "Error occurred while fetching colors. " + e.getMessage();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);

        return responseObject.toString();
    }

    public String updateColor(HttpServletRequest req, ColorDTO colorDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject dataObject = new JsonObject();

        String colorIdParam = req.getParameter("id");
        if (colorIdParam == null || colorIdParam.isBlank()) {
            message = "Color ID is required!";
        } else if (colorDTO.getName() == null || colorDTO.getName().isBlank()) {
            message = "Color Name is required!";
        } else if (colorDTO.getHexCode() == null || colorDTO.getHexCode().isBlank()) {
            message = "Hex Code is required!";
        } else if (!HEX_CODE_PATTERN.matcher(colorDTO.getHexCode().trim()).matches()) {
            message = "Invalid Hex Code format. It should start with # followed by 6 hex characters.";
        } else {
            colorDTO.setId(Integer.parseInt(colorIdParam));
            try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                Color existingColor = hibernateSession.createNamedQuery("Color.findById", Color.class)
                        .setParameter("id", colorDTO.getId())
                        .getSingleResultOrNull();
                if (existingColor != null) {
                    Transaction transaction = hibernateSession.beginTransaction();
                    try {
                        existingColor.setName(colorDTO.getName().trim());
                        existingColor.setHexCode(colorDTO.getHexCode().trim());
                        hibernateSession.merge(existingColor);
                        transaction.commit();
                        status = true;
                        message = "Color updated successfully!";
                        dataObject.addProperty("id", existingColor.getId());
                        dataObject.addProperty("name", existingColor.getName());
                        dataObject.addProperty("hexCode", existingColor.getHexCode());
                    } catch (Exception e) {
                        if (transaction != null) {
                            transaction.rollback();
                        }
                        message = "Error occurred while updating color. " + e.getMessage();
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

    public String deleteColor(HttpServletRequest req) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        String colorIdParam = req.getParameter("id");
        if (colorIdParam == null || colorIdParam.isBlank()) {
            message = "Color ID is required for deletion!";
        } else {
            try {
                int colorId = Integer.parseInt(colorIdParam);
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Color existingColor = hibernateSession.createNamedQuery("Color.findById", Color.class)
                            .setParameter("id", colorId)
                            .getSingleResultOrNull();
                    if (existingColor != null) {
                        Transaction transaction = hibernateSession.beginTransaction();
                        try {
                            hibernateSession.remove(existingColor);
                            transaction.commit();
                            status = true;
                            message = "Color deleted successfully!";
                        } catch (Exception e) {
                            if (transaction != null) {
                                transaction.rollback();
                            }
                            message = "Error occurred while deleting color. " + e.getMessage();
                        } finally {
                            hibernateSession.close();
                        }
                    } else {
                        message = "Color not found.";
                    }
                }
            } catch (NumberFormatException e) {
                message = "Invalid Color ID format.";
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
