package com.paymybuddy.backend.controller.web;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.service.FriendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FriendController {

    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendService friendService;

    @GetMapping("/add-friend")
    public String addFriendPage() {
        logger.debug("GET request received for /add-friend");
        return "add-friend";
    }

    @PostMapping("/add-friend")
    public String addFriend(@RequestParam String friendEmail, Model model) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User '{}' is attempting to add friend '{}'", userEmail, friendEmail);

        String result = friendService.addFriend(userEmail, friendEmail);
        logger.debug("Add friend result: {}", result);

        model.addAttribute("message", result);
        return "redirect:/add-friend";
    }
}
