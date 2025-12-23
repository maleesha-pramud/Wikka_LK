package com.wigerlabs.wikka_lk.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/test")
public class TestController {
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response test() {
        return Response.ok("{\"status\": true}").build();
    }
}
