package com.paymybuddy.backend.dto;

import java.math.BigDecimal;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private BigDecimal balance;

    public UserDTO(Long id, String username, String email, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.balance = balance;
    }
}
