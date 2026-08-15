package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.dto.ProductDTO;
import com.wigerlabs.wikka_lk.service.ProductService;
import com.wigerlabs.wikka_lk.util.AppUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
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
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeProductStatus(@Context HttpServletRequest req, String jsonData) {
        ProductDTO productDTO = AppUtil.GSON.fromJson(jsonData, ProductDTO.class);
        String responseJson = new ProductService().changeProductStatus(req, productDTO);
        return Response.ok().entity(responseJson).build();
    }

    @POST
    @Path("/{id}/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadImage(
            @PathParam("id") int productId,
            @org.glassfish.jersey.media.multipart.FormDataParam("file") java.io.InputStream fileInputStream,
            @org.glassfish.jersey.media.multipart.FormDataParam("file") org.glassfish.jersey.media.multipart.FormDataContentDisposition fileMetaData,
            @Context HttpServletRequest req) {
        String responseJson = new ProductService().uploadImage(productId, fileInputStream, fileMetaData, req);
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Path("/{id}/image/{imageId}/main")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setMainImage(@PathParam("id") int productId, @PathParam("imageId") int imageId) {
        String responseJson = new ProductService().setMainImage(productId, imageId);
        return Response.ok().entity(responseJson).build();
    }

    @DELETE
    @Path("/{id}/image/{imageId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteImage(@PathParam("id") int productId, @PathParam("imageId") int imageId, @Context HttpServletRequest req) {
        String responseJson = new ProductService().deleteImage(productId, imageId, req);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewProduct(@Context HttpServletRequest req) {
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
