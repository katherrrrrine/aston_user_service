package com.example.service;

import com.example.dao.UserDao;
import com.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@example.com", 30);
        testUser.setId(1L);
    }

    @Test
    void shouldCreateUser() {
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return null;
        }).when(userDao).save(any(User.class));

        Long userId = userService.createUser(testUser);

        assertEquals(1L, userId);
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void shouldGetUserById() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        when(userDao.findById(999L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(999L);

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void shouldGetAllUsers() {
        when(userDao.findAll()).thenReturn(List.of(testUser));

        List<User> users = userService.getAllUsers();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    void shouldUpdateUser() {
        User updatedUser = new User("Updated Name", "updated@example.com", 35);
        updatedUser.setId(1L);

        userService.updateUser(updatedUser);

        verify(userDao, times(1)).update(updatedUser);
    }

    @Test
    void shouldDeleteUserWhenExists() {
        User user = new User("Test", "test@example.com", 30);
        user.setId(1L);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).delete(userCaptor.capture());

        assertEquals(1L, userCaptor.getValue().getId());
    }
}