package com.example.service;

import com.example.dto.UserCreateDto;
import com.example.dto.UserDto;
import com.example.entity.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("Test User");
        createDto.setEmail("test@example.com");
        createDto.setAge(25);

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setAge(25);
        savedUser.setCreatedAt(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(createDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(25, result.getAge());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserById() {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setAge(25);
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        UserDto result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void getUserByIdExceptionWhenNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserById(999));
        verify(userRepository, times(1)).findById(999);
    }

    @Test
    void getAllUsers() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");

        User user2 = new User();
        user2.setId(2);
        user2.setName("User 2");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("User 1", result.get(0).getName());
        assertEquals("User 2", result.get(1).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser() {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setAge(30);

        UserCreateDto updateDto = new UserCreateDto();
        updateDto.setName("New Name");
        updateDto.setEmail("new@example.com");
        updateDto.setAge(35);

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.updateUser(1, updateDto);

        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(35, result.getAge());

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUserExceptionWhenUserNotFound() {
        UserCreateDto updateDto = new UserCreateDto();
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(999, updateDto));
        verify(userRepository, times(1)).findById(999);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser() {
        doNothing().when(userRepository).deleteById(1);
        userService.deleteUser(1);
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void convertToDTO() {
        User user = new User();
        user.setId(1);
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setAge(30);
        user.setCreatedAt(LocalDateTime.now());

        UserDto dto = userService.convertToDTO(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getAge(), dto.getAge());
    }
}