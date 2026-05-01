package com.example.restaurant.service;

import com.example.restaurant.model.Cart;
import com.example.restaurant.model.CartItem;
import com.example.restaurant.model.Dish;
import com.example.restaurant.model.User;

public interface CartService {
    Cart getCartByUser(User user);
    Cart getCartByUserId(Long userId);
    CartItem addItemToCart(Long userId, Long dishId, int quantity);
    CartItem updateCartItemQuantity(Long cartItemId, int quantity);
    void removeItemFromCart(Long cartItemId);
    void clearCart(Long userId);
    double calculateCartTotal(Long userId);
    Cart saveCart(Cart cart);
}