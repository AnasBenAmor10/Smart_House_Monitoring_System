package tn.supcom.tos.smarthouse.services;

import jakarta.ejb.EJBException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import tn.supcom.tos.smarthouse.entities.User;
import tn.supcom.tos.smarthouse.enums.Role;
import tn.supcom.tos.smarthouse.repositories.UserRepository;
import tn.supcom.tos.smarthouse.utils.Argon2Utils;
import tn.supcom.tos.smarthouse.DTO.UpdateDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@ApplicationScoped
public class UserServices {

    @Inject
    UserRepository userRepository;

    @Inject
    Argon2Utils argon2Utils;

    // Register a new user
    public void registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new EJBException("User with email " + user.getEmail() + " already exists");
        }
        user.setCreated_on(LocalDateTime.now().toString());
        user.setRoles(Collections.singleton(Role.USER));
        user.hashPassword(user.getPassword(), argon2Utils);
        userRepository.save(user);
    }

    // Authenticate an existing user
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EJBException("User not found"));

        if (argon2Utils.check(user.getPassword(), password.toCharArray())) {
            return user;
        }
        throw new EJBException("Failed log in with email: " + email + " [Unknown email or wrong password]");
    }

    // Delete a user by email
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EJBException("User with email " + email + " not found"));
        userRepository.delete(user);
    }

    // Update a user's details
    public void updateUser(String email, UpdateDto updateDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EJBException("User with email " + email + " not found"));

        // Update user properties
        user.setUsername(updateDto.getUsername());
        user.setEmail(updateDto.getEmail());

        // Hash the password only if it is provided
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            user.hashPassword(updateDto.getPassword(), argon2Utils);
        }
        // Set update timestamp
        user.setUpdatedAt(LocalDateTime.now().toString());
        userRepository.save(user);
    }
}
