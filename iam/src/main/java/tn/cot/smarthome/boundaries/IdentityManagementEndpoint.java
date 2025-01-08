package tn.cot.smarthome.boundaries;

import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import tn.cot.smarthome.entities.Identity;
import tn.cot.smarthome.services.IdentityServices;

@Path("/identities")
public class IdentityManagementEndpoint {

    @Inject
    IdentityServices identityService;

    // Update Identity
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateIdentity(@PathParam("id") Long id, Identity updatedIdentity,
                                   @QueryParam("currentPassword") String currentPassword,
                                   @QueryParam("newPassword") String newPassword) {
        try {
            // Ensure the identity exists
            Identity existingIdentity = identityService.getIdentityById(id);
            if (existingIdentity == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Identity not found with ID: " + id)
                        .build();
            }

            // Update the identity with the new values
            identityService.updateIdentity(id, updatedIdentity.getUsername(), updatedIdentity.getEmail(), newPassword, currentPassword);

            // Return success response with updated identity
            return Response.ok(updatedIdentity).build();
        } catch (EJBException e) {
            // Return an error if the password validation fails
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
    // Delete Identity (User)
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteIdentity(@PathParam("id") Long id) {
        try {
            // Delete the identity by ID
            identityService.deleteIdentityById(id);

            // Return success response
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (EJBException e) {
            // Return an error if deletion fails
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Identity not found with ID: " + id)
                    .build();
        }
    }
}
