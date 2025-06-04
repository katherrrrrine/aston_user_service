package com.example.dao;

import com.example.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    void save(User user);
    Optional<User> findById(Long id);  // Изменено на Optional
    List<User> findAll();
    void update(User user);
    void delete(User user);
}