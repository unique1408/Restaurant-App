package com.example.restaurant.service;

import com.example.restaurant.model.User;
import com.example.restaurant.model.Role;
import com.example.restaurant.model.Cart;
import com.example.restaurant.repository.UserRepository;
import com.example.restaurant.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Override
    public User registerUser(User user) {
        // Set default role if not set
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        
        // Save user first
        User savedUser = userRepository.save(user);
        
        // Create cart for user
        Cart cart = new Cart(savedUser);
        cartRepository.save(cart);
        savedUser.setCart(cart);
        
        return userRepository.save(savedUser);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public User changeUserRole(Long userId, Role newRole) {
        User user = findById(userId);
        user.setRole(newRole);
        return userRepository.save(user);
    }
}