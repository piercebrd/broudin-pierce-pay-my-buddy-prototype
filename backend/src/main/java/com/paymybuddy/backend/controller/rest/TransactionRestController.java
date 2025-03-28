package com.paymybuddy.backend.controller.rest;

import com.paymybuddy.backend.dto.TransactionDTO;
import com.paymybuddy.backend.entity.Transaction;
import com.paymybuddy.backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAll();
    }

    @PostMapping
    public TransactionDTO createTransaction(@RequestBody Transaction tx) {
        Transaction saved = transactionService.save(tx);
        return new TransactionDTO(
                saved.getId(),
                saved.getSender().getId(),
                saved.getReceiver().getId(),
                saved.getDescription(),
                saved.getAmount(),
                saved.getCreatedAt()
        );
    }

    @GetMapping("/user/{userId}")
    public List<TransactionDTO> getUserTransactions(@PathVariable Long userId) {
        return transactionService.getUserTransactions(userId);
    }
}
