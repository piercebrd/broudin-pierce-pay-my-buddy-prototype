package com.paymybuddy.backend.controller.web;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class HtmlAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profilePage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register-form")
    public String registerForm(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email already in use.");
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password
        user.setBalance(BigDecimal.ZERO);
        userRepository.save(user);

        return "redirect:/login";
    }


    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String username,
                                @RequestParam String password,
                                Model model,
                                HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("user");

        user.setUsername(username);
        if (!password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepository.save(user);
        session.setAttribute("user", user);  // Update session user
        model.addAttribute("message", "Profile updated successfully.");
        return "redirect:/profile";
    }
}
