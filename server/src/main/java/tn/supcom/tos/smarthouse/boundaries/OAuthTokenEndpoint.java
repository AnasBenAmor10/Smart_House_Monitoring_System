package tn.supcom.tos.smarthouse.boundaries;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import tn.supcom.tos.smarthouse.enums.Role;
import tn.supcom.tos.smarthouse.security.JwtManager;
import tn.supcom.tos.smarthouse.utils.Oauth2PKCE;
import java.util.Map;
import java.util.Set;


@Path("/oauth/token")
public class OAuthTokenEndpoint {

    @Inject
    JwtManager jwtManager;
    @Inject
    Oauth2PKCE oauth2Pkce;

    @GET
    public Response generateToken(@QueryParam("authorization_code") String authorizationCode,
                                  @QueryParam("code_verifier")String codeVerifier){
        try {

            Map<String, Object> cred = oauth2Pkce.CheckChallenge(authorizationCode, codeVerifier);
            String tenantId = (String) cred.get("tenantId");
            String subject = (String) cred.get("subject");
            String approvedScopes = (String) cred.get("approvedScopes");
            Set<Role> roles = (Set<Role>) cred.get("roles");
            String[] rolesArray = roles.stream()
                    .map(Role::name)
                    .toArray(String[]::new);
            var token =  jwtManager.generateToken(tenantId, subject, approvedScopes, rolesArray);
            return Response
                    .ok(Json.createObjectBuilder()
                            .add("accessToken", token)
                            .add("tokenType", "Bearer")
                            .add("expiresIn", 1020)
                            .build())
                    .build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"message\":\""+e.getMessage()+"\"}").build();
        }

    }
}