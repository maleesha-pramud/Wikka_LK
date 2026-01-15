package com.wigerlabs.wikka_lk.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wigerlabs.wikka_lk.dto.CategoryDTO;
import com.wigerlabs.wikka_lk.entity.Category;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CategoryService {
    public String addCategory(CategoryDTO categoryDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Category existingCategory = hibernateSession.createNamedQuery("Category.findByName", Category.class)
                    .setParameter("name", categoryDTO.getName())
                    .getSingleResultOrNull();
            if (existingCategory != null) {
                message = "Category with this name already exists.";
            } else {
                Category newCategory = new Category();
                newCategory.setName(categoryDTO.getName());

                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    hibernateSession.persist(newCategory);
                    transaction.commit();
                    status = true;
                    message = "Category added successfully";
                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                    }
                    message = "Error occurred while adding category. " + e.getMessage();
                } finally {
                    hibernateSession.close();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return responseObject.toString();
    }

    public String getAllCategories() {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonArray dataArray = new JsonArray();

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            List<Category> categoryList = hibernateSession.createQuery("FROM Category", Category.class).list();
            for (Category category : categoryList) {
                JsonObject categoryObj = new JsonObject();
                categoryObj.addProperty("id", category.getId());
                categoryObj.addProperty("name", category.getName());
                if (category.getCreatedAt() != null) {
                    categoryObj.addProperty("createdAt", category.getCreatedAt().toString());
                }
                if (category.getUpdatedAt() != null) {
                    categoryObj.addProperty("updatedAt", category.getUpdatedAt().toString());
                }
                dataArray.add(categoryObj);
            }
            status = true;
            message = "Categories fetched successfully!";
        } catch (Exception e) {
            message = "Error occurred while fetching categories. " + e.getMessage();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);

        return responseObject.toString();
    }

    public String updateCategory(CategoryDTO categoryDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject dataObject = new JsonObject();

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Category existingCategory = hibernateSession.createNamedQuery("Category.findById", Category.class)
                    .setParameter("id", categoryDTO.getId())
                    .getSingleResultOrNull();
            if (existingCategory != null) {
                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    existingCategory.setName(categoryDTO.getName());
                    hibernateSession.merge(existingCategory);
                    transaction.commit();
                    status = true;
                    message = "Category updated successfully!";
                    dataObject.addProperty("id", existingCategory.getId());
                    dataObject.addProperty("name", existingCategory.getName());
                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                    }
                    message = "Error occurred while updating category. " + e.getMessage();
                } finally {
                    hibernateSession.close();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataObject);

        return responseObject.toString();
    }

    public String deleteCategory(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            int categoryId = Integer.parseInt(request.getParameter("id"));
            Category existingCategory = hibernateSession.find(Category.class, categoryId);
            if (existingCategory != null) {
                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    hibernateSession.remove(existingCategory);
                    transaction.commit();
                    status = true;
                    message = "Category deleted successfully!";
                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                    }
                    message = "Error occurred while deleting category. " + e.getMessage();
                } finally {
                    hibernateSession.close();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return responseObject.toString();
    }
}
