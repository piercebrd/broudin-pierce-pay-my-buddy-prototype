package com.paymybuddy.backend.service;

import com.paymybuddy.backend.entity.Connection;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    public List<Connection> getConnectionsForUser(User user) {
        return connectionRepository.findByUser(user);
    }

    public boolean isAlreadyConnected(User user, User friend) {
        return connectionRepository.existsByUserAndFriend(user, friend);
    }

    public Connection save(Connection connection) {
        return connectionRepository.save(connection);
    }

    public void delete(Long id) {
        connectionRepository.deleteById(id);
    }
}
