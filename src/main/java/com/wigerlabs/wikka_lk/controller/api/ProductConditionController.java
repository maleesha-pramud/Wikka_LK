package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.service.ProductConditionService;
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
}
