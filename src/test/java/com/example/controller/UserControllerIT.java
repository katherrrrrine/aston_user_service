package com.example.controller;

import com.example.entity.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerIT {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withDatabaseName("users")
            .withUsername("postgres")
            .withPassword("password");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private User createTestUser(String name, String email, int age) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Test
    void returnCreatedUser() throws Exception {
        String userJson = "{" +
                "\"name\":\"test\"," +
                "\"email\":\"test@example.com\"," +
                "\"age\":30" +
                "}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.age", is(30)));
    }

    @Test
    void getUserById() throws Exception {
        User savedUser = createTestUser("test", "test@test.com", 25);

        mockMvc.perform(get("/api/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedUser.getId())))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.email", is("test@test.com")))
                .andExpect(jsonPath("$.age", is(25)));
    }

    @Test
    void getAllUsers() throws Exception {
        createTestUser("User1", "user1@test.com", 20);
        createTestUser("User2", "user2@test.com", 30);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("User1")))
                .andExpect(jsonPath("$[1].name", is("User2")));
    }

    @Test
    void updateUser() throws Exception {
        User existingUser = createTestUser("Old Name", "old@test.com", 30);

        String updateJson = "{" +
                "\"name\":\"New Name\"," +
                "\"email\":\"new@test.com\"," +
                "\"age\":35" +
                "}";

        mockMvc.perform(put("/api/users/{id}", existingUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Name")))
                .andExpect(jsonPath("$.email", is("new@test.com")))
                .andExpect(jsonPath("$.age", is(35)));
    }

    @Test
    void deleteUser() throws Exception {
        User user = createTestUser("To Delete", "delete@example.com", 40);

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(user.getId()));
    }
}