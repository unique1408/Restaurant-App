package com.example.restaurant.service;

import com.example.restaurant.model.User;
import com.example.restaurant.model.Role;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User findById(Long id);
    List<User> findAllUsers();
    User updateUser(User user);
    void deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User changeUserRole(Long userId, Role newRole);
}