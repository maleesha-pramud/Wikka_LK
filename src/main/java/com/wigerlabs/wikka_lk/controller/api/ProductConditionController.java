package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.service.ProductConditionService;
import jakarta.persistence.NamedQuery;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/product-condition")
public class ProductConditionController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProductConditions() {
        String responseJson = new ProductConditionService().getAllProductConditions();
        return Response.ok().entity(responseJson).build();
    }

    @jakarta.ws.rs.POST
    @jakarta.ws.rs.Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProductCondition(String jsonData) {
        com.wigerlabs.wikka_lk.dto.ProductConditionDTO dto = com.wigerlabs.wikka_lk.util.AppUtil.GSON.fromJson(jsonData, com.wigerlabs.wikka_lk.dto.ProductConditionDTO.class);
        String responseJson = new ProductConditionService().addProductCondition(dto);
        return Response.ok().entity(responseJson).build();
    }

    @jakarta.ws.rs.PUT
    @jakarta.ws.rs.Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProductCondition(String jsonData, @jakarta.ws.rs.core.Context jakarta.servlet.http.HttpServletRequest req) {
        com.wigerlabs.wikka_lk.dto.ProductConditionDTO dto = com.wigerlabs.wikka_lk.util.AppUtil.GSON.fromJson(jsonData, com.wigerlabs.wikka_lk.dto.ProductConditionDTO.class);
        String responseJson = new ProductConditionService().updateProductCondition(req, dto);
        return Response.ok().entity(responseJson).build();
    }

    @jakarta.ws.rs.DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProductCondition(@jakarta.ws.rs.core.Context jakarta.servlet.http.HttpServletRequest req) {
        String responseJson = new ProductConditionService().deleteProductCondition(req);
        return Response.ok().entity(responseJson).build();
    }
}
