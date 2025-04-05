package com.paymybuddy.backend.config;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);

    private final UserRepository userRepository;

    public LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = authentication.getName();
        logger.info("Authentication success for user: {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("Authenticated user not found in database: {}", email);
            return new RuntimeException("User not found after login");
        });

        request.getSession().setAttribute("user", user);
        logger.debug("User session initialized for: {}", email);

        response.sendRedirect("/home");
        logger.info("Redirected {} to /home", email);
    }
}
