package com.paymybuddy.backend.controller.web;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.service.FriendService;
import com.paymybuddy.backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendService friendService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/home")
    public String showHomePage(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        System.out.println("Transactions: " + transactionService.findBySender(user));

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
        try {
            transactionService.processTransfer(friendEmail, amount, description);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return showHomePage(model);
        }

        return "redirect:/home";
    }

}
