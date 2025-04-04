package com.paymybuddy.backend.controller.web;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.service.FriendService;
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
    private FriendService friendService;

    @GetMapping("/add-friend")
    public String addFriendPage() {
        return "add-friend";
    }

    @PostMapping("/add-friend")
    public String addFriend(@RequestParam String friendEmail, Model model) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String result = friendService.addFriend(userEmail, friendEmail);

        model.addAttribute("message", result);
        return "redirect:/add-friend";
    }


}
