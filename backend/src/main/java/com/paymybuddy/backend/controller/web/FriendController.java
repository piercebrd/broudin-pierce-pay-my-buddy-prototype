package com.paymybuddy.backend.controller.web;

import com.paymybuddy.backend.entity.Connection;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.ConnectionRepository;
import com.paymybuddy.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FriendController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    @GetMapping("/add-friend")
    public String addFriendPage() {
        return "add-friend";
    }

    @PostMapping("/add-friend")
    public String addFriend(@RequestParam String friendEmail, Model model) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        User friend = userRepository.findByEmail(friendEmail).orElse(null);

        if (friend == null) {
            model.addAttribute("error", "No user found with this email.");
            return "add-friend";
        }

        if (user.getId().equals(friend.getId())) {
            model.addAttribute("error", "You can't add yourself!");
            return "add-friend";
        }

        if (connectionRepository.existsByUserAndFriend(user, friend)) {
            model.addAttribute("error", "You're already connected.");
            return "add-friend";
        }

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);
        connectionRepository.save(connection);

        model.addAttribute("message", "Friend added successfully!");
        return "add-friend";
    }

}
