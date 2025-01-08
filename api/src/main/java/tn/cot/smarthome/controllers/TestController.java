package tn.cot.smarthome.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;


@Path("/")
public class TestController {
    @Path("/test")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response testServer() {
        return Response.ok("{\"message\": \"Server is running!\"}")
                .build();
    }
}
