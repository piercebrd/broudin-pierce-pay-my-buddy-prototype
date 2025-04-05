package com.paymybuddy.backend.integration;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class HtmlAuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void loginPageLoads() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void registerPageLoads() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void logoutRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void successfulRegistrationRedirectsToLogin() throws Exception {
        mockMvc.perform(post("/register-form")
                        .param("username", "newuser")
                        .param("email", "newuser@example.com")
                        .param("password", "pass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void registrationWithExistingEmailShowsError() throws Exception {
        mockMvc.perform(post("/register-form")
                        .param("username", "Alice2")
                        .param("email", "alice@example.com") // already in test-data.sql
                        .param("password", "pass123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithUserDetails(value = "alice@example.com", userDetailsServiceBeanName = "customUserDetailsService")
    void profilePageLoadsForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void profilePageRedirectsForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithUserDetails(value = "alice@example.com", userDetailsServiceBeanName = "customUserDetailsService")
    void editProfilePageLoadsForAuthenticatedUser() throws Exception {
        User alice = userRepository.findByEmail("alice@example.com").orElseThrow();

        mockMvc.perform(get("/profile/edit")
                        .sessionAttr("user", alice))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"))
                .andExpect(model().attributeExists("user"));
    }


    @Test
    @WithUserDetails(value = "alice@example.com", userDetailsServiceBeanName = "customUserDetailsService")
    void editProfileSubmitRedirectsToProfile() throws Exception {
        User alice = userRepository.findByEmail("alice@example.com").orElseThrow();

        mockMvc.perform(post("/profile/edit")
                        .param("username", "AliceUpdated")
                        .param("password", "newpass123")
                        .sessionAttr("user", alice))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    }

}
