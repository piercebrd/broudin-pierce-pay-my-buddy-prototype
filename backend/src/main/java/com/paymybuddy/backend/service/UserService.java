package com.paymybuddy.backend.service;

import com.paymybuddy.backend.dto.UserDTO;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // This constructor is not needed anymore since jwtUtil is not used
    // public UserService(JwtUtil jwtUtil, UserRepository userRepository) {
    //     this.jwtUtil = jwtUtil;
    //     this.userRepository = userRepository;
    // }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // Method to get the currently authenticated user based on session
    public UserDTO getCurrentUser() {
        // Fetch user from Spring Security context
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername(); // Assuming the username is the email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBalance()
        );
    }
}
