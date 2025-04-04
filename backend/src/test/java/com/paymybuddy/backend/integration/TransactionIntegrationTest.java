package com.paymybuddy.backend.integration;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User alice;
    private User bob;

    @BeforeEach
    void setup() {
        alice = new User();
        alice.setUsername("alice");
        alice.setEmail("alice@example.com");
        alice.setPassword("$2a$10$encoded"); // Dummy encoded password
        alice.setBalance(BigDecimal.valueOf(100));
        userRepository.save(alice);

        bob = new User();
        bob.setUsername("bob");
        bob.setEmail("bob@example.com");
        bob.setPassword("$2a$10$encoded");
        bob.setBalance(BigDecimal.valueOf(50));
        userRepository.save(bob);
    }

    @Test
    @WithUserDetails(value = "alice@example.com", userDetailsServiceBeanName = "customUserDetailsService")
    void testProcessTransfer_success() throws Exception {
        mockMvc.perform(post("/transfer")
                        .param("friendEmail", "bob@example.com")
                        .param("amount", "20")
                        .param("description", "Lunch"))
                .andExpect(redirectedUrl("/home"));

        User updatedAlice = userRepository.findByEmail("alice@example.com").orElseThrow();
        User updatedBob = userRepository.findByEmail("bob@example.com").orElseThrow();

        assertEquals(BigDecimal.valueOf(80), updatedAlice.getBalance());
        assertEquals(BigDecimal.valueOf(70), updatedBob.getBalance());
    }
}
