package com.paymybuddy.backend.service;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<String> register(String username, String email, String rawPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            return Optional.of("Email already in use.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setBalance(BigDecimal.ZERO);

        userRepository.save(user);
        return Optional.empty();
    }

    public User getSessionUser(HttpSession session) {
        return (session != null) ? (User) session.getAttribute("user") : null;
    }

    public Optional<User> updateProfile(User user, String newUsername, String newPassword) {
        user.setUsername(newUsername);
        if (!newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        return Optional.of(userRepository.save(user));
    }
}
