package tn.cot.smarthome.services;

import jakarta.ejb.EJBException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import tn.cot.smarthome.entities.Identity;
import tn.cot.smarthome.enums.Role;
import tn.cot.smarthome.repositories.IdentityRepository;
import tn.cot.smarthome.security.Argon2Utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.internal.authentication.AwsCredentialHelper.LOGGER;

@ApplicationScoped
public class IdentityServices {

    @Inject
    private IdentityRepository identityRepository;

    @Inject
    private Argon2Utils argon2Utils;

    @Inject
    private EmailService emailService;

    private static final String ACTIVATION_EMAIL_SENDER = "benamoranas2001@gmail.com";
    private static final String ACTIVATION_EMAIL_SUBJECT = "Activate Account";
    private static final String ACTIVATION_EMAIL_TEMPLATE =
            "Welcome to your Smart House Monitoring System! Activate your account now and start your journey with us.\n\n" +
                    "Your Activation Code: %s\n\n" +
                    "This code will expire in 5 minutes.";
    private static final int ACTIVATION_CODE_LENGTH = 6;
    private static final int ACTIVATION_CODE_EXPIRATION_MINUTES = 5;

    private final Map<String, Pair<String, LocalDateTime>> activationCodes = new HashMap<>();
    public void registerIdentity(String username, String password, String email) {
        validateIdentity(username, email);
//        LOGGER.info("Registering identity: " + username);
//        LOGGER.info("Password: " + password);
//        LOGGER.info("Email: " + email);

        Identity identity = new Identity();
        identity.setUsername(username);
        identity.setPassword(password);
        identity.setEmail(email);
        identity.setCreationDate(LocalDateTime.now().toString());
        identity.setRoles(Role.R_P00.getValue());
        identity.setScopes("resource:read resource:write");
        identity.hashPassword(password, argon2Utils);
//        Identity identity = createNewIdentity(username, password, email);
        LOGGER.info("Saving identity: " + identity);
        identityRepository.save(identity);
        // Logger to verify save into mongodb
        LOGGER.info("Verifying the saved identity...");
        identityRepository.findById(identity.getId())
                .ifPresentOrElse(
                        savedIdentity -> LOGGER.info("Identity found in MongoDB: " + savedIdentity),
                        () -> LOGGER.error("Failed to find the saved identity in MongoDB.")
                );
        String activationCode = generateActivationCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(ACTIVATION_CODE_EXPIRATION_MINUTES);
        activationCodes.put(activationCode, Pair.of(identity.getEmail(), expirationTime));

//        String message = String.format(ACTIVATION_EMAIL_TEMPLATE, activationCode);
//        emailService.sendEmail(ACTIVATION_EMAIL_SENDER, identity.getEmail(), ACTIVATION_EMAIL_SUBJECT, message);
    }
    public void activateIdentity(String code) {
        Pair<String, LocalDateTime> codeDetails = activationCodes.get(code);

        if (codeDetails == null) {
            throw new EJBException("Invalid activation code.");
        }

        String email = codeDetails.getLeft();
        LocalDateTime expirationTime = codeDetails.getRight();

        if (LocalDateTime.now().isAfter(expirationTime)) {
            activationCodes.remove(code);
            deleteIdentityByEmail(email);
            throw new EJBException("Activation code expired.");
        }

        Identity identity = identityRepository.findByEmail(email).orElseThrow(() ->
                new EJBException("Identity associated with the activation code not found."));
        identity.setAccountActivated(true);
        identityRepository.save(identity);
        activationCodes.remove(code);
    }

    private void validateIdentity(String username, String email) {
        if (identityRepository.findByUsername(username).isPresent() ) {
            throw new EJBException("An identity with username '" + username + "' already exists.");
        }
        if (identityRepository.findByEmail(email).isPresent()) {
            throw new EJBException("An identity with email '" + email + "' already exists.");
        }
        if (username == null || username.isEmpty()) {
            throw new EJBException("Username is required.");
        }
        if (email == null || email.isEmpty()) {
            throw new EJBException("Email is required.");
        }
    }

//    private Identity createNewIdentity(String username, String password, String email) {
//        Identity identity = new Identity();
//        identity.setUsername(username);
//        identity.setPassword(password);
//        identity.setEmail(email);
//        identity.setCreationDate(LocalDateTime.now().toString());
//        identity.setRoles(Role.R_P00.getValue());
//        identity.setScopes("resource:read resource:write");
//        identity.hashPassword(password, argon2Utils);
////        LOGGER.info("Created new identity with ID: " + identity.getId());
//        return identity;
//    }
    private void deleteIdentityByEmail(String email) {
        identityRepository.findByEmail(email).ifPresent(identityRepository::delete);
    }

    private String generateActivationCode() {
        String characters = "0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder codeBuilder = new StringBuilder(ACTIVATION_CODE_LENGTH);

        for (int i = 0; i < ACTIVATION_CODE_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}
