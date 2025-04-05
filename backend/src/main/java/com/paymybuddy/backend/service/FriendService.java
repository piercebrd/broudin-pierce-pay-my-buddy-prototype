package com.paymybuddy.backend.service;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendService {

    private static final Logger logger = LoggerFactory.getLogger(FriendService.class);

    @Autowired
    private UserRepository userRepository;

    public String addFriend(String userEmail, String friendEmail) {
        logger.info("User '{}' is attempting to add friend '{}'", userEmail, friendEmail);

        User user = userRepository.findByEmail(userEmail).orElseThrow();
        User friend = userRepository.findByEmail(friendEmail).orElse(null);

        if (friend == null) {
            logger.warn("No user found with email '{}'", friendEmail);
            return "No user found with this email.";
        }

        if (user.getId().equals(friend.getId())) {
            logger.warn("User '{}' attempted to add themselves as a friend", userEmail);
            return "You can't add yourself!";
        }

        if (user.getConnections().contains(friend)) {
            logger.info("User '{}' is already connected with '{}'", userEmail, friendEmail);
            return "Already connected.";
        }

        user.getConnections().add(friend);
        friend.getConnections().add(user); // Ensure bidirectionality

        userRepository.save(user);
        userRepository.save(friend);

        logger.info("User '{}' and '{}' are now connected", userEmail, friendEmail);
        return "Friend added successfully!";
    }

    public List<User> getFriends(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<User> connections = new ArrayList<>(user.getConnections());
        logger.debug("Retrieved {} friend(s) for user ID {}", connections.size(), userId);
        return connections;
    }

    public String removeFriend(Long userId, Long friendId) {
        logger.info("User ID {} is attempting to remove friend ID {}", userId, friendId);

        User user = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();

        if (!user.getConnections().remove(friend)) {
            logger.warn("No connection found between user ID {} and friend ID {}", userId, friendId);
            return "Connection not found.";
        }

        userRepository.save(user);
        logger.info("Connection removed between user ID {} and friend ID {}", userId, friendId);
        return "Connection removed.";
    }
}
