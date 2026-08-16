package com.wigerlabs.wikka_lk.controller.api;

import com.wigerlabs.wikka_lk.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/order")
public class OrderController {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrder(String jsonData) {
        String responseJson = new OrderService().placeOrder(jsonData);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Path("/buyer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersByBuyer(@Context HttpServletRequest req) {
        String responseJson = new OrderService().getOrdersByBuyer(req);
        return Response.ok().entity(responseJson).build();
    }
}
