package tn.cot.smarthome.controllers;

import jakarta.ejb.EJB;
import tn.cot.smarthome.entities.Alert;
import tn.cot.smarthome.entities.Coordinates;
import tn.cot.smarthome.entities.Identity;
import tn.cot.smarthome.repositories.AlertRepository;
import tn.cot.smarthome.repositories.CoordinatesRepository;
import tn.cot.smarthome.repositories.IdentityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.cot.smarthome.security.JwtManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/alerts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AlertController{
    @Inject
    CoordinatesRepository coordinatesRepository;
    @Inject
    IdentityRepository identityRepository;
    @Inject
    AlertRepository alertRepository;
    @EJB
    private JwtManager jwtManager;

    @GET
    public Response scanForAlerts(@HeaderParam("Authorization") String authHeader) {
        try {
            String userEmail = extractUserEmailFromToken(authHeader);
            List<Alert> alertsToSend = sendAlertsIfApplicable(userEmail);
            return Response.ok(alertsToSend).build();
        } catch (Exception ex) {
            return Response.status(400, ex.getMessage()).build();
        }
    }
    // This function aim to extract the email of the active user
    public String extractUserEmailFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Bad Token Format");
        }

        String token = authorizationHeader.substring("Bearer ".length());

        try {
            // Verify the token and extract claims
            var claims = jwtManager.verifyToken(token);
            String username = claims.get("sub");  // 'sub' contains the username

            // Fetch the Identity based on the username
            Optional<Identity> identity = identityRepository.findByUsername(username);

            if (identity.isPresent()) {
                return identity.get().getEmail();  // Return the user's email
            } else {
                throw new NotAuthorizedException("User not found");
            }

        } catch (Exception e) {
            throw new NotAuthorizedException("Invalid or expired token");
        }
    }

    private List<Alert> sendAlertsIfApplicable(String userEmail) {
        List<Alert> alertsToSend = new ArrayList<>();

        // Retrieve all identities
        List<Identity> users = identityRepository.findAll().collect(Collectors.toList());

        // Retrieve all alerts
        List<Alert> alerts = alertRepository.findAll().collect(Collectors.toList());

        // Filter out the abnormal alerts (those with abnormal behavior)
        List<Alert> abnormalAlerts = alerts.stream()
                .filter(alert -> alert.getAbnormalBehaviour() == 1)
                .collect(Collectors.toList());

        // Loop through each abnormal alert
        for (Alert alert : abnormalAlerts) {
            // Find the nearest 3 users to the alert (this method should be defined elsewhere)
            List<Identity> nearestUsers = findNearestUsers(alert, users, 3);

            // Check if the userEmail is in the list of nearest users
            boolean userIncluded = nearestUsers.stream()
                    .anyMatch(user -> user.getEmail().equals(userEmail));

            // If the user is included, add the alert to the list
            if (userIncluded) {
                alertsToSend.add(alert);
            }
        }
        return alertsToSend;
    }


    // Method to find the nearest N users to an alert based on location (latitude and longitude)
    private List<Identity> findNearestUsers(Alert alert, List<Identity> users, int n) {
        return users.stream()
                .sorted(Comparator.comparingDouble(user -> calculateDistance(alert, user)))
                .limit(n)
                .collect(Collectors.toList());
    }

    // Method to calculate the distance between an alert and a user based on their locations
    private double calculateDistance(Alert alert, Identity user) {
        // Fetch the user's coordinates based on their email
        Optional<Coordinates> userCoordOpt = coordinatesRepository.findByEmail(user.getEmail());

        // If user coordinates are not found, return a large value indicating an error
        if (!userCoordOpt.isPresent()) {
            return 1e9;
        }

        // Retrieve the user's coordinates

        Coordinates userCoord = userCoordOpt.get();

        // Convert latitude and longitude from degrees to radians
        double lat1 = Math.toRadians(alert.getLatitude());
        double lon1 = Math.toRadians(alert.getLongitude());
        double lat2 = Math.toRadians(userCoord.getLatitude());
        double lon2 = Math.toRadians(userCoord.getLongitude());

        // Haversine formula to calculate the great-circle distance
        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Radius of Earth in kilometers
        double radius = 6371.0;

        // Calculate and return the distance between the alert and the user
        return radius * c;
    }

}