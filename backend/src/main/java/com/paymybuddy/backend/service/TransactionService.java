package com.paymybuddy.backend.service;

import com.paymybuddy.backend.entity.Transaction;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> findBySender(User sender) {
        return transactionRepository.findSenderById(sender.getId());
    }

    public List<Transaction> findByReceiver(User receiver) {
        return transactionRepository.findReceiverById(receiver.getId());
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }
}
