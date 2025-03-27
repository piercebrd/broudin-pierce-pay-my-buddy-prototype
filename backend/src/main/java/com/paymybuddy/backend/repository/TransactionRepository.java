package com.paymybuddy.backend.repository;

import com.paymybuddy.backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findSenderById(long senderId);
    List<Transaction> findReceiverById(long receiverId);
}
