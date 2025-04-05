package com.paymybuddy.backend.controller.web;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HtmlAuthController {

    private static final Logger logger = LoggerFactory.getLogger(HtmlAuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/login")
    public String loginPage() {
        logger.debug("GET /login requested");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        request.getSession().invalidate();
        logger.info("User '{}' has logged out", email);
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        logger.debug("GET /register requested");
        return "register";
    }

    @PostMapping("/register-form")
    public String registerForm(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        logger.info("User registration attempt for: {}", email);
        var error = authService.register(username, email, password);
        if (error.isPresent()) {
            logger.warn("Registration failed for '{}': {}", email, error.get());
            model.addAttribute("error", error.get());
            return "register";
        }

        logger.info("User '{}' registered successfully", email);
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        logger.info("Profile page loaded for user: {}", email);
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model, HttpServletRequest request) {
        User user = authService.getSessionUser(request.getSession(false));
        if (user == null) {
            logger.warn("Unauthenticated access attempt to /profile/edit");
            return "redirect:/login";
        }

        logger.debug("Editing profile for user: {}", user.getEmail());
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String username,
                                @RequestParam String password,
                                Model model,
                                HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        User user = authService.getSessionUser(session);
        if (user == null) {
            logger.warn("Unauthenticated POST to /profile/edit");
            return "redirect:/login";
        }

        logger.info("User '{}' is updating their profile", user.getEmail());

        authService.updateProfile(user, username, password)
                .ifPresent(updated -> {
                    session.setAttribute("user", updated);
                    logger.debug("Profile updated for user: {}", updated.getEmail());
                });

        return "redirect:/profile";
    }
}
