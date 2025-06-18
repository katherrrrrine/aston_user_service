package com.example.controller;

import com.example.dto.UserCreateDto;
import com.example.dto.UserDto;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto createTestUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1);
        dto.setName("Test User");
        dto.setEmail("test@example.com");
        dto.setAge(25);
        dto.setCreatedAt(LocalDateTime.now());
        return dto;
    }

    private UserCreateDto createTestUserCreateDto() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("Test User");
        dto.setEmail("test@example.com");
        dto.setAge(25);
        return dto;
    }

    @Test
    void createUser() throws Exception {
        UserCreateDto requestDto = createTestUserCreateDto();
        UserDto responseDto = createTestUserDto();

        Mockito.when(userService.createUser(Mockito.any(UserCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getUserById() throws Exception {
        UserDto responseDto = createTestUserDto();
        Mockito.when(userService.getUserById(1)).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getAllUsers() throws Exception {
        UserDto user1 = createTestUserDto();
        UserDto user2 = createTestUserDto();
        user2.setId(2);
        user2.setName("Another User");

        List<UserDto> users = Arrays.asList(user1, user2);

        Mockito.when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test User"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Another User"));
    }

    @Test
    void updateUser() throws Exception {
        UserCreateDto updateDto = createTestUserCreateDto();
        updateDto.setName("Updated Name");

        UserDto updatedDto = createTestUserDto();
        updatedDto.setName("Updated Name");

        Mockito.when(userService.updateUser(1, updateDto)).thenReturn(updatedDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(1);
    }


}