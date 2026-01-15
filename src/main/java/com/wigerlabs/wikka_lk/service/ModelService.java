package com.wigerlabs.wikka_lk.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wigerlabs.wikka_lk.dto.ModelDTO;
import com.wigerlabs.wikka_lk.entity.Brand;
import com.wigerlabs.wikka_lk.entity.Model;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ModelService {
    public String addModel(ModelDTO modelDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Model existingModel = hibernateSession.createNamedQuery("Model.findByName", Model.class)
                .setParameter("name", modelDTO.getName())
                .getSingleResultOrNull();
        if (existingModel != null) {
            message = "A model with this name already exists.";
        } else {
            Brand existingBrand = hibernateSession.createNamedQuery("Brand.findById", Brand.class)
                    .setParameter("id", modelDTO.getBrandId())
                    .getSingleResultOrNull();
            if (existingBrand != null) {
                Model newModel = new Model();
                newModel.setName(modelDTO.getName().trim());
                newModel.setBrand(existingBrand);

                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    hibernateSession.persist(newModel);
                    transaction.commit();
                    status = true;
                    message = "Model added successfully!";
                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                    }
                    message = "Error occurred while adding model.";
                } finally {
                    hibernateSession.close();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }

    public String getAllModels() {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonArray dataArray = new JsonArray();

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            List<Model> modelList = hibernateSession.createQuery("From Model", Model.class).list();
            for (Model model : modelList) {
                JsonObject modelObj = new JsonObject();
                modelObj.addProperty("id", model.getId());
                modelObj.addProperty("name", model.getName());
                modelObj.addProperty("brandId", model.getBrand().getId());
                modelObj.addProperty("brandName", model.getBrand().getName());
                if (model.getCreatedAt() != null) {
                    modelObj.addProperty("createdAt", model.getCreatedAt().toString());
                }
                if (model.getUpdatedAt() != null) {
                    modelObj.addProperty("updatedAt", model.getUpdatedAt().toString());
                }
                dataArray.add(modelObj);
            }
            status = true;
            message = "Models fetched successfully!";
        } catch (Exception e) {
            message = "Error occurred while fetching models. " + e.getMessage();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);

        return responseObject.toString();
    }

    public String updateModel(ModelDTO modelDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject dataObject = new JsonObject();

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Model existingModel = hibernateSession.createNamedQuery("Model.findById", Model.class)
                    .setParameter("id", modelDTO.getId())
                    .getSingleResultOrNull();
            if (existingModel != null) {
                Brand existingBrand = hibernateSession.createNamedQuery("Brand.findById", Brand.class)
                        .setParameter("id", modelDTO.getBrandId())
                        .getSingleResultOrNull();
                if (existingBrand != null) {
                    Transaction transaction = hibernateSession.beginTransaction();
                    try {
                        existingModel.setName(modelDTO.getName().trim());
                        existingModel.setBrand(existingBrand);
                        hibernateSession.merge(existingModel);
                        transaction.commit();
                        status = true;
                        message = "Model updated successfully!";
                        dataObject.addProperty("id", existingModel.getId());
                        dataObject.addProperty("name", existingModel.getName());
                        dataObject.addProperty("brandId", existingModel.getBrand().getId());
                    } catch (Exception e) {
                        if (transaction != null) {
                            transaction.rollback();
                        }
                        message = "Error occurred while updating model.";
                    } finally {
                        hibernateSession.close();
                    }
                } else {
                    message = "Brand not found!";
                }
            } else {
                message = "Model not found!";
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataObject);

        return responseObject.toString();
    }

    public String deleteModel(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        String modelIdParam = request.getParameter("id");
        if (modelIdParam == null || modelIdParam.isBlank()) {
            message = "Model ID is required for deletion.";
        } else {
            try {
                int modelId = Integer.parseInt(modelIdParam);
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Model existingModel = hibernateSession.find(Model.class, modelId);
                    if (existingModel != null) {
                        Transaction transaction = hibernateSession.beginTransaction();
                        try {
                            hibernateSession.remove(existingModel);
                            transaction.commit();
                            status = true;
                            message = "Model deleted successfully!";
                        } catch (Exception e) {
                            if (transaction != null) {
                                transaction.rollback();
                            }
                            message = "Error occurred while deleting model.";
                        }
                    }
                }
            } catch (NumberFormatException e) {
                message = "Invalid Model ID format.";
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }
}
