package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.dto.BrandDTO;
import com.wigerlabs.wikka_lk.service.BrandService;
import com.wigerlabs.wikka_lk.util.AppUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/brand")
public class BrandController {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBrand(String jsonData) {
        BrandDTO brandDTO = AppUtil.GSON.fromJson(jsonData, BrandDTO.class);
        String responseJson = new BrandService().addBrand(brandDTO);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBrands() {
        String responseJson = new BrandService().getAllBrands();
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBrand(String jsonData, @Context HttpServletRequest request) {
        BrandDTO brandDTO = AppUtil.GSON.fromJson(jsonData, BrandDTO.class);
        String responseJson = new BrandService().updateBrand(brandDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBrand(@Context HttpServletRequest request) {
        String responseJson = new BrandService().deleteBrand(request);
        return Response.ok().entity(responseJson).build();
    }
}
