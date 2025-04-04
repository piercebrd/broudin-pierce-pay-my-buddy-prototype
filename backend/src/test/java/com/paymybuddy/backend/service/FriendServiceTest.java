package com.paymybuddy.backend.service;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendService friendService;

    private User user;
    private User friend;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        friend = new User();
        friend.setId(2L);
        friend.setEmail("friend@example.com");
    }

    @Test
    void addFriend_shouldReturnError_whenFriendNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("friend@example.com")).thenReturn(Optional.empty());

        String result = friendService.addFriend("user@example.com", "friend@example.com");

        assertEquals("No user found with this email.", result);
    }

    @Test
    void addFriend_shouldReturnError_whenAddingSelf() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        String result = friendService.addFriend("user@example.com", "user@example.com");

        assertEquals("You can't add yourself!", result);
    }

    @Test
    void addFriend_shouldReturnError_whenAlreadyConnected() {
        user.getConnections().add(friend);
        friend.getConnections().add(user);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("friend@example.com")).thenReturn(Optional.of(friend));

        String result = friendService.addFriend("user@example.com", "friend@example.com");

        assertEquals("Already connected.", result);
    }

    @Test
    void addFriend_shouldAddBothWays_whenValid() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("friend@example.com")).thenReturn(Optional.of(friend));

        String result = friendService.addFriend("user@example.com", "friend@example.com");

        assertEquals("Friend added successfully!", result);
        assertTrue(user.getConnections().contains(friend));
        assertTrue(friend.getConnections().contains(user));
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).save(friend);
    }

    @Test
    void getFriends_shouldReturnConnections() {
        user.getConnections().add(friend);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<User> friends = friendService.getFriends(1L);

        assertEquals(1, friends.size());
        assertEquals(friend, friends.get(0));
    }

    @Test
    void removeFriend_shouldReturnSuccess_whenExists() {
        user.getConnections().add(friend);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(friend));

        String result = friendService.removeFriend(1L, 2L);

        assertEquals("Connection removed.", result);
        assertFalse(user.getConnections().contains(friend));
        verify(userRepository).save(user);
    }

    @Test
    void removeFriend_shouldReturnError_whenNotConnected() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(friend));

        String result = friendService.removeFriend(1L, 2L);

        assertEquals("Connection not found.", result);
        verify(userRepository, never()).save(user);
    }
}
