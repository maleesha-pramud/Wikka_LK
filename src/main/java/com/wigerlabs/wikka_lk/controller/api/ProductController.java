package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.dto.ProductDTO;
import com.wigerlabs.wikka_lk.service.ProductService;
import com.wigerlabs.wikka_lk.util.AppUtil;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/product")
public class ProductController {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProduct(String jsonData) {
        ProductDTO productDTO = AppUtil.GSON.fromJson(jsonData, ProductDTO.class);
        String responseJson = new ProductService().addProduct(productDTO);
        return Response.ok().entity(responseJson).build();
    }
}
