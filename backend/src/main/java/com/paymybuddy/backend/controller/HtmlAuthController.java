package com.paymybuddy.backend.controller;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
                            Model model,
                            HttpServletResponse response) {

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            User userObj = user.get();
            String jwt = jwtUtil.generateToken(email);

            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setHttpOnly(true);       // Prevent JS access
            jwtCookie.setPath("/");            // Make it available everywhere
            jwtCookie.setMaxAge(60 * 60);      // 1 hour
            response.addCookie(jwtCookie);     // ✅ Add it to the response

            return "redirect:/home";
        }

        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null) ? auth.getName() : "Guest";
        model.addAttribute("email", email);
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/login";
    }
}
