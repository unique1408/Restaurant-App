package com.example.restaurant.controller;

import com.example.restaurant.model.User;
import com.example.restaurant.model.Role;
import com.example.restaurant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, 
                               RedirectAttributes redirectAttributes) {
        
        // Check if username already exists
        if (userService.existsByUsername(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Username already exists!");
            return "redirect:/user/register";
        }
        
        // Check if email already exists
        if (userService.existsByEmail(user.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email already registered!");
            return "redirect:/user/register";
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role
        user.setRole(Role.USER);
        
        // Register user
        userService.registerUser(user);
        
        redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
        return "redirect:/login";
    }
    
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        // Get current logged in user (you'll need to get this from SecurityContext)
        // For now, we'll just show the form
        return "profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user, 
                                RedirectAttributes redirectAttributes) {
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/user/profile";
    }
}