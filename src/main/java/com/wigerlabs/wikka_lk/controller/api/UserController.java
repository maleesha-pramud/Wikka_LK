package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.dto.UserDTO;
import com.wigerlabs.wikka_lk.service.UserService;
import com.wigerlabs.wikka_lk.util.AppUtil;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
public class UserController {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String jsonData) {
        UserDTO userDTO = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().createUser(userDTO);
        return Response.ok().entity(responseJson).build();
    }
}
