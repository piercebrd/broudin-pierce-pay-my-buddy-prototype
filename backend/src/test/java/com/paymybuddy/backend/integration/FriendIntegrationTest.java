package com.paymybuddy.backend.integration;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FriendIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithUserDetails(value = "alice@example.com", userDetailsServiceBeanName = "customUserDetailsService")
    void testAddFriend_success() throws Exception {
        mockMvc.perform(post("/add-friend")
                        .param("friendEmail", "bob@example.com"))
                .andExpect(redirectedUrl("/add-friend"));

        User updatedAlice = userRepository.findByEmail("alice@example.com").orElseThrow();
        Set<User> connections = updatedAlice.getConnections();

        assertEquals(1, connections.size());
        assertTrue(connections.stream().anyMatch(u -> u.getEmail().equals("bob@example.com")));
    }
}
