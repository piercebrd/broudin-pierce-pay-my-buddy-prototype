package com.paymybuddy.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String description;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public TransactionDTO(Long id, Long senderId, Long receiverId, String description, BigDecimal amount, LocalDateTime createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.description = description;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getSenderId() { return senderId; }
    public Long getReceiverId() { return receiverId; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}
