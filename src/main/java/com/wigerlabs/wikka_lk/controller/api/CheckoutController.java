package com.wigerlabs.wikka_lk.controller.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wigerlabs.wikka_lk.service.CheckoutService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/checkout")
public class CheckoutController {

    @POST
    @Path("/hash")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateHash(String jsonData) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            
            if (!jsonObject.has("orderId")) {
                JsonObject error = new JsonObject();
                error.addProperty("status", false);
                error.addProperty("message", "Invalid payload. 'orderId' is required.");
                return Response.status(Response.Status.BAD_REQUEST).entity(error.toString()).build();
            }

            String orderIdStr = jsonObject.get("orderId").getAsString();
            
            com.google.gson.JsonArray itemsArray = new com.google.gson.JsonArray();
            if (jsonObject.has("items")) {
                itemsArray = jsonObject.getAsJsonArray("items");
            }
            
            String responseJson = new CheckoutService().generateHash(orderIdStr, itemsArray);
            return Response.ok().entity(responseJson).build();
        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("status", false);
            error.addProperty("message", "Server error while processing payload.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error.toString()).build();
        }
    }

    @POST
    @Path("/notify")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response notifyPayment(
            @FormParam("merchant_id") String merchantId,
            @FormParam("order_id") String orderId,
            @FormParam("payhere_amount") String payhereAmount,
            @FormParam("payhere_currency") String payhereCurrency,
            @FormParam("status_code") String statusCode,
            @FormParam("md5sig") String md5sig,
            @FormParam("custom_1") String custom1,
            @FormParam("custom_2") String custom2,
            @FormParam("payment_id") String paymentId) {
            
        new CheckoutService().processNotify(orderId, paymentId, statusCode, md5sig, payhereAmount, payhereCurrency);
        
        // PayHere expects a 200 OK response upon receiving the notification
        return Response.ok().build();
    }
}
