package com.paymybuddy.backend.controller;

import com.paymybuddy.backend.dto.UserDTO;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/{userId}/balance")
    public BigDecimal getUserBalance(@PathVariable Long userId) {
        return userService.findById(userId)
                .map(User::getBalance)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "User deleted.";
    }

    @GetMapping("/me")
    public UserDTO getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        return userService.getCurrentUser(authHeader);
    }
}
