package com.paymybuddy.backend.service;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendService {

    @Autowired
    private UserRepository userRepository;

    public String addFriend(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        User friend = userRepository.findByEmail(friendEmail).orElse(null);

        if (friend == null) {
            return "No user found with this email.";
        }

        if (user.getId().equals(friend.getId())) {
            return "You can't add yourself!";
        }

        if (user.getConnections().contains(friend)) {
            return "Already connected.";
        }

        user.getConnections().add(friend);
        friend.getConnections().add(user); // Ensure bidirectionality

        userRepository.save(user);
        userRepository.save(friend);

        return "Friend added successfully!";
    }



    public List<User> getFriends(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return new ArrayList<>(user.getConnections());
    }

    public String removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();

        if (!user.getConnections().remove(friend)) {
            return "Connection not found.";
        }

        userRepository.save(user);
        return "Connection removed.";
    }
}

