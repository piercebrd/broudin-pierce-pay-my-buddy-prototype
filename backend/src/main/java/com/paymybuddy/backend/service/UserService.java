package com.paymybuddy.backend.service;

import com.paymybuddy.backend.dto.UserDTO;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public List<User> findAllUsers() {
        logger.debug("Fetching all users from the database");
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        logger.debug("Searching for user with ID {}", id);
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        logger.debug("Searching for user with email '{}'", email);
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        logger.info("Saving user '{}'", user.getEmail());
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        logger.info("Deleting user with ID {}", id);
        userRepository.deleteById(id);
    }

    public UserDTO getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername(); // Assuming the username is the email
        logger.debug("Retrieving current authenticated user '{}'", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email '{}'", email);
                    return new RuntimeException("User not found");
                });

        logger.info("Current user '{}' loaded successfully", email);
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBalance()
        );
    }
}
