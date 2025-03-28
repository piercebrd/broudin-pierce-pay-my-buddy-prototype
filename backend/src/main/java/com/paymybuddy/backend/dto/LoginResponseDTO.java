package com.paymybuddy.backend.dto;

public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String email;

    public LoginResponseDTO(String token, Long userId, String email) {
        this.token = token;
        this.userId = userId;
        this.email = email;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }

}
