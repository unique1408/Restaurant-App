package com.example.restaurant.controller;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.OrderRepository;
import com.example.restaurant.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userService.findByUsername(username).orElse(null);
        }

        return null;
    }

    @GetMapping("/orders")
    public String viewOrders(Model model) {

        User user = getCurrentUser();

        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderRepository.findByUser(user);

        model.addAttribute("orders", orders);

        return "orders";
    }
}