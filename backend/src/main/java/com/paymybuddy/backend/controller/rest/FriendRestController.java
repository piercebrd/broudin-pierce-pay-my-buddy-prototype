package com.paymybuddy.backend.controller.rest;

import com.paymybuddy.backend.dto.UserDTO;
import com.paymybuddy.backend.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
public class FriendRestController {

    @Autowired
    private FriendService friendService;

    @GetMapping("/{userId}")
    public List<UserDTO> getFriends(@PathVariable Long userId) {
        return friendService.getFriends(userId).stream()
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getEmail(), u.getBalance()))
                .collect(Collectors.toList());
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
