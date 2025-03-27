package com.paymybuddy.backend.service;

import com.paymybuddy.backend.entity.Connection;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.ConnectionRepository;
import com.paymybuddy.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<User> getFriends(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Connection> connections = connectionRepository.findByUser(user);
        return connections.stream()
                .map(Connection::getFriend)
                .collect(Collectors.toList());
    }

    public String addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) return "You cannot add yourself as a friend.";

        User user = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();

        if (connectionRepository.existsByUserAndFriend(user, friend)) {
            return "Already connected.";
        }

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setFriend(friend);
        connectionRepository.save(connection);
        return "Connection created.";
    }

    public void removeFriend(Long connectionId) {
        connectionRepository.deleteById(connectionId);
    }
}
