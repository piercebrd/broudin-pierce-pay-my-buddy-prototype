package com.paymybuddy.backend.service;

import com.paymybuddy.backend.entity.Transaction;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.TransactionRepository;
import com.paymybuddy.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> findBySender(User sender) {
        return transactionRepository.findSenderById(sender.getId());
    }

    public List<Transaction> findByReceiver(User receiver) {
        return transactionRepository.findReceiverById(receiver.getId());
    }

    public Transaction save(Transaction transaction) {
        Long senderId = transaction.getSender().getId();
        Long receiverId = transaction.getReceiver().getId();

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        BigDecimal amount = transaction.getAmount();

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        // Update balances
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        // Save users and transaction
        userRepository.save(sender);
        userRepository.save(receiver);

        return transactionRepository.save(transaction);
    }


    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }
}
