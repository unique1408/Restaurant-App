package com.example.restaurant.controller;

import com.example.restaurant.model.Cart;
import com.example.restaurant.model.CartItem;
import com.example.restaurant.model.Order;
import com.example.restaurant.model.OrderItem;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.OrderRepository;
import com.example.restaurant.service.CartService;
import com.example.restaurant.service.UserService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;
    
    @GetMapping("/checkout")
    public String checkout(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = cartService.getCartByUser(user);

        if (cart == null) {
            cart = new Cart();
        }

        model.addAttribute("cart", cart);
        model.addAttribute("user", user);   // ⭐ IMPORTANT FIX

        return "checkout";
    }
    
    @PostMapping("/place-order")
    public String placeOrder() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = cartService.getCartByUser(user);

        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(cart.getTotalPrice());
        order.setStatus(Order.OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setDish(cartItem.getDish());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getDish().getPrice().doubleValue());
            orderItem.setSubtotal(cartItem.getSubtotal());

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);

        orderRepository.save(order);

        cart.getItems().clear();

        return "redirect:/orders";
    }
}