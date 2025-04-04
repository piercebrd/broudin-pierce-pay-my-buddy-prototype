package com.paymybuddy.backend.service;

import com.paymybuddy.backend.entity.Transaction;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.TransactionRepository;
import com.paymybuddy.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@example.com");
        sender.setBalance(BigDecimal.valueOf(100));

        receiver = new User();
        receiver.setId(2L);
        receiver.setEmail("receiver@example.com");
        receiver.setBalance(BigDecimal.valueOf(50));
    }

    @Test
    void save_shouldThrow_whenSenderNotFound() {
        Transaction tx = new Transaction();
        tx.setSender(sender);
        tx.setReceiver(receiver);
        tx.setAmount(BigDecimal.TEN);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class, () -> transactionService.save(tx));
        assertEquals("Sender not found", e.getMessage());
    }

    @Test
    void save_shouldThrow_whenInsufficientFunds() {
        sender.setBalance(BigDecimal.valueOf(5));

        Transaction tx = new Transaction();
        tx.setSender(sender);
        tx.setReceiver(receiver);
        tx.setAmount(BigDecimal.TEN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> transactionService.save(tx));
        assertEquals("Insufficient funds.", e.getMessage());
    }

    @Test
    void save_shouldDeductAndSave_whenValid() {
        Transaction tx = new Transaction();
        tx.setSender(sender);
        tx.setReceiver(receiver);
        tx.setAmount(BigDecimal.TEN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(tx);

        Transaction result = transactionService.save(tx);

        assertEquals(BigDecimal.valueOf(90), sender.getBalance());
        assertEquals(BigDecimal.valueOf(60), receiver.getBalance());

        verify(userRepository).save(sender);
        verify(userRepository).save(receiver);
        verify(transactionRepository).save(tx);
        assertEquals(tx, result);
    }

    @Test
    void findBySender_shouldCallRepository() {
        when(transactionRepository.findBySenderId(1L)).thenReturn(List.of(new Transaction()));

        List<Transaction> result = transactionService.findBySender(sender);

        assertEquals(1, result.size());
        verify(transactionRepository).findBySenderId(1L);
    }

    @Test
    void findByReceiver_shouldCallRepository() {
        when(transactionRepository.findByReceiverId(2L)).thenReturn(List.of(new Transaction()));

        List<Transaction> result = transactionService.findByReceiver(receiver);

        assertEquals(1, result.size());
        verify(transactionRepository).findByReceiverId(2L);
    }

    @Test
    void getAll_shouldReturnAllTransactions() {
        List<Transaction> txs = List.of(new Transaction(), new Transaction());
        when(transactionRepository.findAll()).thenReturn(txs);

        assertEquals(2, transactionService.getAll().size());
        verify(transactionRepository).findAll();
    }
}
