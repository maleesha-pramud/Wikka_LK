package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.dto.ProductDTO;
import com.wigerlabs.wikka_lk.service.ProductService;
import com.wigerlabs.wikka_lk.util.AppUtil;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
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

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProducts() {
        String responseJson = new ProductService().getAllProducts();
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Path("/change-status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeProductStatus(String jsonData) {
        ProductDTO productDTO = AppUtil.GSON.fromJson(jsonData, ProductDTO.class);
        String responseJson = new ProductService().changeProductStatus(productDTO);
        return Response.ok().entity(responseJson).build();
    }
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewProduct(@jakarta.ws.rs.core.Context jakarta.servlet.http.HttpServletRequest req) {
        String responseJson = new ProductService().viewProduct(req);
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProduct(String jsonData, @jakarta.ws.rs.core.Context jakarta.servlet.http.HttpServletRequest req) {
        ProductDTO productDTO = AppUtil.GSON.fromJson(jsonData, ProductDTO.class);
        String responseJson = new ProductService().updateProduct(req, productDTO);
        return Response.ok().entity(responseJson).build();
    }

    @jakarta.ws.rs.DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProduct(@jakarta.ws.rs.core.Context jakarta.servlet.http.HttpServletRequest req) {
        String responseJson = new ProductService().deleteProduct(req);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Path("/seller")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductsBySeller(@jakarta.ws.rs.core.Context jakarta.servlet.http.HttpServletRequest req) {
        String responseJson = new ProductService().getProductsBySeller(req);
        return Response.ok().entity(responseJson).build();
    }
}
