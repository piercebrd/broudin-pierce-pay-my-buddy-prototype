package com.paymybuddy.backend.service;

import com.paymybuddy.backend.dto.TransactionDTO;
import com.paymybuddy.backend.entity.Transaction;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.TransactionRepository;
import com.paymybuddy.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> findBySender(User sender) {
        logger.debug("Fetching transactions for sender ID {}", sender.getId());
        return transactionRepository.findBySenderId(sender.getId());
    }

    public List<Transaction> findByReceiver(User receiver) {
        logger.debug("Fetching transactions for receiver ID {}", receiver.getId());
        return transactionRepository.findByReceiverId(receiver.getId());
    }

    @Transactional
    public Transaction save(Transaction transaction) {
        Long senderId = transaction.getSender().getId();
        Long receiverId = transaction.getReceiver().getId();
        BigDecimal amount = transaction.getAmount();

        logger.info("Saving transaction from user {} to user {} for {}€", senderId, receiverId, amount);

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> {
                    logger.error("Sender with ID {} not found", senderId);
                    return new RuntimeException("Sender not found");
                });
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> {
                    logger.error("Receiver with ID {} not found", receiverId);
                    return new RuntimeException("Receiver not found");
                });

        if (sender.getBalance().compareTo(amount) < 0) {
            logger.warn("Sender ID {} has insufficient funds for {}€ transfer", senderId, amount);
            throw new IllegalArgumentException("Insufficient funds.");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);

        logger.debug("Balances updated: sender {} → {}, receiver {} → {}",
                senderId, sender.getBalance(), receiverId, receiver.getBalance());

        return transactionRepository.save(transaction);
    }

    public List<TransactionDTO> getUserTransactions(Long userId) {
        logger.debug("Getting all transactions for user ID {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User ID {} not found when retrieving transactions", userId);
                    return new RuntimeException("User not found");
                });

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
        logger.debug("Fetching all transactions from database");
        return transactionRepository.findAll();
    }

    public void processTransfer(String friendEmail, BigDecimal amount, String description) {
        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User '{}' is initiating a transfer of {}€ to '{}'", senderEmail, amount, friendEmail);

        User sender = userRepository.findByEmail(senderEmail).orElseThrow(() -> {
            logger.error("Sender '{}' not found", senderEmail);
            return new RuntimeException("Sender not found");
        });
        User receiver = userRepository.findByEmail(friendEmail).orElseThrow(() -> {
            logger.error("Receiver '{}' not found", friendEmail);
            return new RuntimeException("Receiver not found");
        });

        if (sender.getBalance().compareTo(amount) < 0) {
            logger.warn("Transfer failed: '{}' has insufficient funds ({}€ available, {}€ requested)",
                    senderEmail, sender.getBalance(), amount);
            throw new IllegalArgumentException("Fonds insuffisants.");
        }

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);
        transactionRepository.save(transaction);

        logger.info("Transfer of {}€ from '{}' to '{}' completed successfully", amount, senderEmail, friendEmail);
    }
}
