package com.paymybuddy.backend.service;

import com.paymybuddy.backend.dto.TransactionDTO;
import com.paymybuddy.backend.entity.Transaction;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.TransactionRepository;
import com.paymybuddy.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> findBySender(User sender) {
        return transactionRepository.findBySenderId(sender.getId());
    }

    public List<Transaction> findByReceiver(User receiver) {
        return transactionRepository.findByReceiverId(receiver.getId());
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

    public List<TransactionDTO> getUserTransactions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> all = transactionRepository.findAll();

        return all.stream()
                .filter(tx -> tx.getSender().getId().equals(userId) || tx.getReceiver().getId().equals(userId))
                .map(tx -> new TransactionDTO(
                        tx.getId(),
                        tx.getSender().getId(),
                        tx.getReceiver().getId(),
                        tx.getDescription(),
                        tx.getAmount(),
                        tx.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }


    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    public void processTransfer(String friendEmail, BigDecimal amount, String description) {
        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByEmail(senderEmail).orElseThrow();
        User receiver = userRepository.findByEmail(friendEmail).orElseThrow();

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Fonds insuffisants.");
        }

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        // Update balances
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);
        transactionRepository.save(transaction);
    }

}
