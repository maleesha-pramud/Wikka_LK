package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.annotation.IsLoggedIn;
import com.wigerlabs.wikka_lk.dto.UserDTO;
import com.wigerlabs.wikka_lk.service.UserService;
import com.wigerlabs.wikka_lk.util.AppUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
public class UserController {
    // User Registration
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String jsonData) {
        UserDTO userDTO = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().createUser(userDTO);
        return Response.ok().entity(responseJson).build();
    }

    // User Login
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonData, @Context HttpServletRequest request) {
        UserDTO userDTO = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().userLogin(userDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    // User Update
    @IsLoggedIn
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(String jsonData, @Context HttpServletRequest request) {
        UserDTO userDTO = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().updateUser(userDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    // User Logout
    @IsLoggedIn
    @Path("/logout")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletRequest request) {
        String responseJson  = new UserService().logoutUser(request);
        return Response.ok().entity(responseJson).build();
    }
}
