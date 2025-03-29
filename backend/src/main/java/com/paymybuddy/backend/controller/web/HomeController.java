package com.paymybuddy.backend.controller.web;

import com.paymybuddy.backend.entity.Transaction;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.service.FriendService;
import com.paymybuddy.backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
        System.out.println("Redirected to /home");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        System.out.println("Transactions: " + transactionService.findBySender(user));


        model.addAttribute("friends", friendService.getFriends(user.getId()));
        model.addAttribute("transactions", transactionService.findBySender(user));
        model.addAttribute("email", email);
        return "home";
    }

    @PostMapping("/transfer")
    public String processTransfer(@RequestParam("friendEmail") String friendEmail,
                                  @RequestParam("amount") BigDecimal amount,
                                  @RequestParam("description") String description) {

        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByEmail(senderEmail).orElseThrow();
        User receiver = userRepository.findByEmail(friendEmail).orElseThrow();

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        transactionService.save(transaction);

        return "redirect:/home";
    }
}
