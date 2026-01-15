package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.dto.ModelDTO;
import com.wigerlabs.wikka_lk.service.ModelService;
import com.wigerlabs.wikka_lk.util.AppUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/model")
public class ModelController {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addModel(String jsonData) {
        ModelDTO modelDTO = AppUtil.GSON.fromJson(jsonData, ModelDTO.class);
        String responseJson = new ModelService().addModel(modelDTO);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllModels() {
        String responseJson = new ModelService().getAllModels();
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateModel(String jsonData) {
        ModelDTO modelDTO = AppUtil.GSON.fromJson(jsonData, ModelDTO.class);
        String responseJson = new ModelService().updateModel(modelDTO);
        return Response.ok().entity(responseJson).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteModel(@Context HttpServletRequest request) {
        String responseJson = new ModelService().deleteModel(request);
        return Response.ok().entity(responseJson).build();
    }
}
