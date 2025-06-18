package com.example.service;

import com.example.dto.UserCreateDto;
import com.example.dto.UserDto;
import com.example.entity.User;
import com.example.exception.ConflictException;
import com.example.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto createUser(UserCreateDto userCreateDTO) {
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new ConflictException("Email уже существует: " + userCreateDTO.getEmail());
        }

        User user = new User();
        user.setName(userCreateDTO.getName());
        user.setEmail(userCreateDTO.getEmail());
        user.setAge(userCreateDTO.getAge());

        user.setCreatedAt(
                userCreateDTO.getCreatedAt() != null
                        ? userCreateDTO.getCreatedAt()
                        : LocalDateTime.now()
        );

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));
        return convertToDTO(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(Integer id, UserCreateDto userCreateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setName(userCreateDTO.getName());
        user.setEmail(userCreateDTO.getEmail());
        user.setAge(userCreateDTO.getAge());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    UserDto convertToDTO(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        return dto;
    }
}