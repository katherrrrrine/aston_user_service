package com.example.repository;

import com.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("users")
            .withUsername("postgres")
            .withPassword("password");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void saveUser() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setAge(30);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("test");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void findUserByEmail() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@example.com");
        user.setAge(25);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("test");
    }

    @Test
    void returnEmptyWhenEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("notexist@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    void checkIfEmailExists() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@example");
        user.setAge(35);
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("test@example")).isTrue();
        assertThat(userRepository.existsByEmail("unknown@example")).isFalse();
    }

    @Test
    void findAllUsers() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        user1.setAge(20);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        user2.setAge(30);

        userRepository.saveAll(List.of(user1, user2));

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getName)
                .containsExactlyInAnyOrder("User1", "User2");
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setName("Original");
        user.setEmail("original@example.com");
        user.setAge(40);
        User savedUser = userRepository.save(user);

        savedUser.setName("Updated");
        savedUser.setEmail("updated@example.com");
        userRepository.save(savedUser);

        Optional<User> updatedUser = userRepository.findById(savedUser.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo("Updated");
        assertThat(updatedUser.get().getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void deleteUser() {
        User user = new User();
        user.setName("ToDelete");
        user.setEmail("delete@example.com");
        user.setAge(50);
        User savedUser = userRepository.save(user);

        userRepository.deleteById(savedUser.getId());

        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }

    @Test
    void returnEmptyIdNotFound() {
        Optional<User> found = userRepository.findById(999);
        assertThat(found).isEmpty();
    }
}