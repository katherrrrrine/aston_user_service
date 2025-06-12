package com.example.dao;

import com.example.entity.User;
import com.example.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    private static UserDao userDao;

    @BeforeAll
    static void setup() {
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());
        System.setProperty("hibernate.connection.pool_size", "5");
        System.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        System.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        userDao = new UserDaoImpl();
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    @Order(1)
    void shouldSaveUser() {
        User user = new User("Test User", "test@example.com", 30);
        userDao.save(user);

        assertNotNull(user.getId());
    }

    @Test
    @Order(2)
    void shouldFindUserById() {
        User newUser = new User("Find User", "find@example.com", 25);
        userDao.save(newUser);

        Optional<User> foundUser = userDao.findById(newUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(newUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    @Order(3)
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> foundUser = userDao.findById(999L);
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @Order(4)
    void shouldFindAllUsers() {
        userDao.save(new User("User 1", "user1@example.com", 20));
        userDao.save(new User("User 2", "user2@example.com", 30));

        List<User> users = userDao.findAll();

        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 2);
    }

    @Test
    @Order(5)
    void shouldUpdateUser() {
        User user = new User("Update User", "update@example.com", 40);
        userDao.save(user);

        user.setName("Updated Name");
        userDao.update(user);

        Optional<User> updatedUser = userDao.findById(user.getId());

        assertTrue(updatedUser.isPresent());
        assertEquals("Updated Name", updatedUser.get().getName());
    }

    @Test
    @Order(6)
    void shouldDeleteUser() {
        User user = new User("Delete User", "delete@example.com", 50);
        userDao.save(user);

        userDao.delete(user);

        Optional<User> deletedUser = userDao.findById(user.getId());
        assertTrue(deletedUser.isEmpty());
    }
    @Test
    @Order(7)
    void saveUserException() {
        SessionFactory originalFactory = HibernateUtil.getSessionFactory();
        try {
            SessionFactory mockFactory = mock(SessionFactory.class);
            when(mockFactory.openSession()).thenThrow(new HibernateException("Database connection failed"));
            HibernateUtil.setSessionFactory(mockFactory);
            User user = new User("Test User", "test@example.com", 30);
            assertThrows(RuntimeException.class, () -> userDao.save(user));
        } finally {
            HibernateUtil.setSessionFactory(originalFactory);
        }
    }

    @Test
    @Order(8)
    void findByIdException() {
        SessionFactory originalFactory = HibernateUtil.getSessionFactory();
        try {
            SessionFactory mockFactory = mock(SessionFactory.class);
            when(mockFactory.openSession()).thenThrow(new HibernateException("Database connection failed"));
            HibernateUtil.setSessionFactory(mockFactory);
            Optional<User> result = userDao.findById(1L);
            assertTrue(result.isEmpty());
        } finally {
            HibernateUtil.setSessionFactory(originalFactory);
        }
    }

    @Test
    @Order(9)
    void updateException() {
        SessionFactory originalFactory = HibernateUtil.getSessionFactory();
        try {
            SessionFactory mockFactory = mock(SessionFactory.class);
            when(mockFactory.openSession()).thenThrow(new HibernateException("Database connection failed"));
            HibernateUtil.setSessionFactory(mockFactory);
            User user = new User("Test User", "test@example.com", 30);
            assertThrows(RuntimeException.class, () -> userDao.update(user));
        } finally {
            HibernateUtil.setSessionFactory(originalFactory);
        }
    }

    @Test
    @Order(10)
    void deleteException() {
        SessionFactory originalFactory = HibernateUtil.getSessionFactory();
        try {
            SessionFactory mockFactory = mock(SessionFactory.class);
            when(mockFactory.openSession()).thenThrow(new HibernateException("Database connection failed"));
            HibernateUtil.setSessionFactory(mockFactory);
            User user = new User("Test User", "test@example.com", 30);
            assertThrows(RuntimeException.class, () -> userDao.delete(user));
        } finally {
            HibernateUtil.setSessionFactory(originalFactory);
        }
    }
}