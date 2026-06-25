package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.dto.ColorDTO;
import com.wigerlabs.wikka_lk.service.ColorService;
import com.wigerlabs.wikka_lk.util.AppUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/color")
public class ColorController {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addColor(String jsonData) {
        ColorDTO colorDTO = AppUtil.GSON.fromJson(jsonData, ColorDTO.class);
        String responseJson = new ColorService().addColor(colorDTO);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllColors() {
        String responseJson = new ColorService().getAllColors();
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateColor(String jsonData, @Context HttpServletRequest req) {
        ColorDTO colorDTO = AppUtil.GSON.fromJson(jsonData, ColorDTO.class);
        String responseJson = new ColorService().updateColor(req, colorDTO);
        return Response.ok().entity(responseJson).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteColor(@Context HttpServletRequest req) {
        String responseJson = new ColorService().deleteColor(req);
        return Response.ok().entity(responseJson).build();
    }
}
