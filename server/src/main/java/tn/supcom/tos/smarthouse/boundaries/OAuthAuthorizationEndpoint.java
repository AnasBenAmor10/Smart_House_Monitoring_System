package tn.supcom.tos.smarthouse.boundaries;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

@Path("/")
public class OAuthAuthorizationEndpoint {
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static SecretKey AESKey;

    // Generate AES key once
    static {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // AES key size
            AESKey = keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Error generating AES key", e);
        }
    }
    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/authorize")
    public Response authorize(@QueryParam("client_id") String client_id) {
        if ( client_id == null || client_id.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("clientId or codeChallenge is missing").build();
        }
        try {
            // Encrypt the client_id using AES-GCM to simulate storing the code challenge securely
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, AESKey, gcmSpec);
            byte[] encryptedChallenge = cipher.doFinal(client_id.getBytes());

            // Convert encrypted challenge to Base64 for storage
            String encryptedChallengeBase64 = Base64.getEncoder().encodeToString(encryptedChallenge);
            String ivBase64 = Base64.getEncoder().encodeToString(iv);
            //Set HttpOnly, Secure, SameSite Cookie with the client_id
            NewCookie cookie = new NewCookie(
                "xssCookie",
                ivBase64 + ":" + encryptedChallengeBase64,
                uriInfo.getBaseUri().getPath(),
                uriInfo.getBaseUri().getHost(),
                "Secure Http Only Cookie",
                3600,
                true,
                true
            );
            // Redirect to the login endpoint
            return Response.status(Response.Status.FOUND)
                    .cookie(cookie)
                    .header("Location", "/login/authorization")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error during encryption").build();
        }

    }

    @POST
    @Path("/login/authorization")
    public Response login(@FormParam("username") String username, @FormParam("password") String password, @Context HttpHeaders headers) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Username or password is missing").build();
        }
        return Response.ok().build();
    }
}