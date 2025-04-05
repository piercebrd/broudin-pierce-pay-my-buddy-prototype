package com.paymybuddy.backend.controller.web;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HtmlAuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    UserRepository userRepository;

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

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register-form")
    public String registerForm(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        var error = authService.register(username, email, password);
        if (error.isPresent()) {
            model.addAttribute("error", error.get());
            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);
        return "profile";
    }



    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model, HttpServletRequest request) {
        User user = authService.getSessionUser(request.getSession(false));
        if (user == null) return "redirect:/login";

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
        if (user == null) return "redirect:/login";

        authService.updateProfile(user, username, password)
                .ifPresent(updated -> session.setAttribute("user", updated));

        return "redirect:/profile";
    }
}
