package com.paymybuddy.backend.service;

import com.paymybuddy.backend.entity.User;
import com.paymybuddy.backend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    }

    @Test
    void register_shouldReturnError_whenEmailAlreadyExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        Optional<String> result = authService.register("Test", "test@example.com", "password");

        assertTrue(result.isPresent());
        assertEquals("Email already in use.", result.get());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldCreateUser_whenEmailIsNew() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        Optional<String> result = authService.register("NewUser", "new@example.com", "password");

        assertTrue(result.isEmpty());
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("NewUser") &&
                        user.getEmail().equals("new@example.com") &&
                        user.getPassword().equals("hashedPassword")
        ));
    }

    @Test
    void updateProfile_shouldUpdateUsernameAndPassword() {
        User user = new User();
        user.setUsername("old");
        user.setPassword("oldPass");

        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        Optional<User> result = authService.updateProfile(user, "new", "newPass");

        assertEquals("new", result.get().getUsername());
        assertEquals("encodedPass", result.get().getPassword());
    }

    @Test
    void updateProfile_shouldUpdateUsernameOnly_whenPasswordIsBlank() {
        User user = new User();
        user.setUsername("old");
        user.setPassword("unchanged");

        Optional<User> result = authService.updateProfile(user, "new", "");

        assertEquals("new", result.get().getUsername());
        assertEquals("unchanged", result.get().getPassword());
    }

    @Test
    void getSessionUser_shouldReturnUser_whenPresent() {
        HttpSession session = mock(HttpSession.class);
        User expectedUser = new User();
        when(session.getAttribute("user")).thenReturn(expectedUser);

        User result = authService.getSessionUser(session);

        assertEquals(expectedUser, result);
    }

    @Test
    void getSessionUser_shouldReturnNull_whenSessionIsNull() {
        assertNull(authService.getSessionUser(null));
    }
}
