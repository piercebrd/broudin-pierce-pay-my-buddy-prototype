package com.paymybuddy.backend.controller;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class HtmlAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login-form")
    public String loginForm(@RequestParam String email,
                            @RequestParam String password,
                            Model model) {

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            String token = jwtUtil.generateToken(email);
            model.addAttribute("token", token);
            model.addAttribute("user", user.get());
            return "redirect:/home";
        }

        model.addAttribute("error", "Invalid email or password");
        return "login";
    }
}
