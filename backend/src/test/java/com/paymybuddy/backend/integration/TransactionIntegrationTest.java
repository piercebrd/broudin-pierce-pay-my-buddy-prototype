package com.paymybuddy.backend.integration;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

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

        assertEquals(0, updatedAlice.getBalance().compareTo(BigDecimal.valueOf(80.0)));
        assertEquals(0, updatedBob.getBalance().compareTo(BigDecimal.valueOf(70.0)));

    }
}

