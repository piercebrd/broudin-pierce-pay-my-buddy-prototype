package com.paymybuddy.backend.service;

import com.paymybuddy.backend.dto.UserDTO;
import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setBalance(BigDecimal.valueOf(100));
    }

    @Test
    void findAllUsers_shouldReturnAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.findAllUsers();

        assertEquals(1, users.size());
        assertEquals("alice", users.get(0).getUsername());
    }

    @Test
    void findById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("alice@example.com", result.get().getEmail());
    }

    @Test
    void findByEmail_shouldReturnUser() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("alice@example.com");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void save_shouldCallRepository() {
        userService.save(user);
        verify(userRepository).save(user);
    }

    @Test
    void deleteById_shouldCallRepository() {
        userService.deleteById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void getCurrentUser_shouldReturnDTO() {
        // Mock security context
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("alice@example.com")
                .password("secret")
                .roles("USER")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        UserDTO currentUser = userService.getCurrentUser();

        assertEquals("alice", currentUser.getUsername());
        assertEquals("alice@example.com", currentUser.getEmail());
        assertEquals(BigDecimal.valueOf(100), currentUser.getBalance());
    }
}
