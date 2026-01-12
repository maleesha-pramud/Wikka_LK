package com.wigerlabs.wikka_lk.middleware;

import com.wigerlabs.wikka_lk.annotation.IsLoggedIn;
import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
@IsLoggedIn
public class AuthFilter implements ContainerRequestFilter {
    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute("user") == null) {
            System.out.println("User not logged in. Sending JSON response.");
            String jsonResponse = "{\"status\": false, \"message\": \"User not logged in\"}";
            containerRequestContext
                    .abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonResponse)
                    .type("application/json")
                    .build());
        }
    }
}
