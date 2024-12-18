package com.trmo.floracare;

import com.trmo.floracare.entities.User;
import com.trmo.floracare.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.trmo.floracare.services.impl.UserServiceImpl;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Должен найти пользователя по идентификатору")
    void testFindById_UserExists() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        Optional<User> result = userService.findById(userId);

        // then
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Должен вернуть optional.empty, если пользователь не найден по идентификатору")
    void testFindById_UserNotFound() {
        // given
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userService.findById(userId);

        // then
        assertFalse(result.isPresent());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Должен найти пользователя по google id")
    void testFindByGoogleId_UserExists() {
        // given
        String googleId = "google123";
        User user = new User();
        user.setGoogleId(googleId);

        when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.of(user));

        // when
        Optional<User> result = userService.findByGoogleId(googleId);

        // then
        assertTrue(result.isPresent());
        assertEquals(googleId, result.get().getGoogleId());
        verify(userRepository).findByGoogleId(googleId);
    }

    @Test
    @DisplayName("Должен вернуть optional.empty, если пользователь не найден по google.id")
    void testFindByGoogleId_UserNotFound() {
        // given
        String googleId = "google123";

        when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userService.findByGoogleId(googleId);

        // then
        assertFalse(result.isPresent());
        verify(userRepository).findByGoogleId(googleId);
    }

    @Test
    @DisplayName("Должен успешно найти пользователя по email")
    void testFindByEmail_UserExists() {
        // given
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        Optional<User> result = userService.findByEmail(email);

        // then
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Должен вернуть optional.empty, если пользователь не найден по email")
    void testFindByEmail_UserNotFound() {
        // given
        String email = "user@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userService.findByEmail(email);

        // then
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Должен успешно сохранить пользователя")
    void testSave_User() {
        // given
        User user = new User();
        user.setEmail("user@example.com");

        when(userRepository.save(user)).thenReturn(user);

        // when
        User result = userService.save(user);

        // then
        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
        verify(userRepository).save(user);
    }
}

