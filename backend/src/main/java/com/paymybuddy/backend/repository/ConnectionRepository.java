package com.paymybuddy.backend.repository;

import com.paymybuddy.backend.entity.Connection;
import com.paymybuddy.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    List<Connection> findByUser(User user);
    List<Connection> findByFriend(User friend);
    boolean existsByUserAndFriend(User user, User friend);
}
