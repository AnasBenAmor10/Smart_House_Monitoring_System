package tn.supcom.tos.smarthouse.controllers;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.supcom.tos.smarthouse.services.UserServices;
import tn.supcom.tos.smarthouse.entities.User;
import jakarta.ejb.EJBException;


@RequestScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserControllers {
    @Inject
    UserServices userServices;

    @POST
    @Path("/signup")
    public Response signup(User user) {
        try {
            this.userServices.registerUser(user);
            return Response.ok(Json.createObjectBuilder().add("message", "User Created successfuly").build()).build();
        }
        catch (EJBException e) {
            return  Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }



}