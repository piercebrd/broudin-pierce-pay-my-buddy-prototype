package com.paymybuddy.backend.service;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<String> register(String username, String email, String rawPassword) {
        logger.info("Registering new user: {}", email);
        if (userRepository.findByEmail(email).isPresent()) {
            logger.warn("Registration failed - email '{}' already in use", email);
            return Optional.of("Email already in use.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setBalance(BigDecimal.TEN);

        userRepository.save(user);
        logger.info("User '{}' registered successfully", email);
        return Optional.empty();
    }

    public User getSessionUser(HttpSession session) {
        if (session == null) {
            logger.warn("Attempted to retrieve user from null session");
            return null;
        }

        User user = (User) session.getAttribute("user");
        if (user != null) {
            logger.debug("Retrieved user '{}' from session", user.getEmail());
        } else {
            logger.warn("No user found in session");
        }

        return user;
    }

    public Optional<User> updateProfile(User user, String newUsername, String newPassword) {
        logger.info("Updating profile for user '{}'", user.getEmail());
        user.setUsername(newUsername);
        if (!newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
            logger.debug("Password updated for user '{}'", user.getEmail());
        } else {
            logger.debug("Password unchanged for user '{}'", user.getEmail());
        }

        User updated = userRepository.save(user);
        logger.info("Profile updated successfully for user '{}'", user.getEmail());
        return Optional.of(updated);
    }
}
