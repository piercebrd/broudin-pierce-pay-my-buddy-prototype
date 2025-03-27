package com.paymybuddy.backend.controller;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @GetMapping("/{userId}")
    public List<User> getFriends(@PathVariable Long userId) {
        return friendService.getFriends(userId);
    }

    @PostMapping("/add")
    public String addFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        return friendService.addFriend(userId, friendId);
    }

    @DeleteMapping("/remove/{connectionId}")
    public String removeFriend(@PathVariable Long connectionId) {
        friendService.removeFriend(connectionId);
        return "Connection removed.";
    }
}
