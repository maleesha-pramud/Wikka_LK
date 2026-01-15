package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.dto.CategoryDTO;
import com.wigerlabs.wikka_lk.service.CategoryService;
import com.wigerlabs.wikka_lk.util.AppUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/category")
public class CategoryController {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCategory(String jsonData) {
        CategoryDTO categoryDTO = AppUtil.GSON.fromJson(jsonData, CategoryDTO.class);
        String responseJson = new CategoryService().addCategory(categoryDTO);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategories() {
        String responseJson = new CategoryService().getAllCategories();
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCategory(String jsonData) {
        CategoryDTO categoryDTO = AppUtil.GSON.fromJson(jsonData, CategoryDTO.class);
        String responseJson = new CategoryService().updateCategory(categoryDTO);
        return Response.ok().entity(responseJson).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCategory(@Context HttpServletRequest request) {
        String responseJson = new CategoryService().deleteCategory(request);
        return Response.ok().entity(responseJson).build();
    }
}
