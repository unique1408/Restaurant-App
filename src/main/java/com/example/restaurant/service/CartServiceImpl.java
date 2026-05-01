package com.example.restaurant.service;

import com.example.restaurant.model.Cart;
import com.example.restaurant.model.CartItem;
import com.example.restaurant.model.Dish;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.CartRepository;
import com.example.restaurant.repository.CartItemRepository;
import com.example.restaurant.repository.DishRepository;
import com.example.restaurant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private DishRepository dishRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found for user: " + user.getUsername()));
    }
    
    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));
    }
    
    @Override
    @Transactional
    public CartItem addItemToCart(Long userId, Long dishId, int quantity) {
        Cart cart = getCartByUserId(userId);
        Dish dish = dishRepository.findById(dishId)
            .orElseThrow(() -> new RuntimeException("Dish not found with id: " + dishId));
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getDish().getId().equals(dishId))
            .findFirst();
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
            updateCartTotal(cart);
            return item;
        } else {
            // Create new cart item
            CartItem newItem = new CartItem(cart, dish, quantity);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
            updateCartTotal(cart);
            return newItem;
        }
    }
    
    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));
        
        if (quantity <= 0) {
            removeItemFromCart(cartItemId);
            return null;
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
            updateCartTotal(item.getCart());
            return item;
        }
    }
    
    @Override
    @Transactional
    public void removeItemFromCart(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        updateCartTotal(cart);
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }
    
    @Override
    public double calculateCartTotal(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getItems().stream()
            .mapToDouble(CartItem::getSubtotal)
            .sum();
    }
    
    @Override
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }
    
    private void updateCartTotal(Cart cart) {
        double total = cart.getItems().stream()
            .mapToDouble(CartItem::getSubtotal)
            .sum();
        cart.setTotalPrice(total);
        cartRepository.save(cart);
    }
}