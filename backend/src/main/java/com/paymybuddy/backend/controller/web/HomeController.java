package com.paymybuddy.backend.controller.web;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.service.FriendService;
import com.paymybuddy.backend.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendService friendService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/home")
    public String showHomePage(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Loading home page for user: {}", email);

        User user = userRepository.findByEmail(email).orElseThrow();
        logger.debug("Retrieved {} transactions for user {}", transactionService.findBySender(user).size(), email);

        model.addAttribute("user", user);
        model.addAttribute("friends", friendService.getFriends(user.getId()));
        model.addAttribute("transactions", transactionService.findBySender(user));
        model.addAttribute("email", email);
        return "home";
    }

    @PostMapping("/transfer")
    public String processTransfer(@RequestParam String friendEmail,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam String description,
                                  Model model) {
        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User '{}' is transferring {}€ to '{}' with description '{}'",
                senderEmail, amount, friendEmail, description);

        try {
            transactionService.processTransfer(friendEmail, amount, description);
        } catch (IllegalArgumentException e) {
            logger.warn("Transfer failed for '{}': {}", senderEmail, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return showHomePage(model);
        }

        logger.info("Transfer from '{}' to '{}' succeeded", senderEmail, friendEmail);
        return "redirect:/home";
    }
}
