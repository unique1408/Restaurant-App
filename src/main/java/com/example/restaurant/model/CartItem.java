package com.example.restaurant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;
    
    private int quantity;
    
    private double subtotal;
    
    public CartItem() {
    }
    
    public CartItem(Cart cart, Dish dish, int quantity) {
        this.cart = cart;
        this.dish = dish;
        this.quantity = quantity;
        this.subtotal = dish.getPrice().doubleValue() * quantity;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Cart getCart() {
        return cart;
    }
    
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    
    public Dish getDish() {
        return dish;
    }
    
    public void setDish(Dish dish) {
        this.dish = dish;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = this.dish.getPrice().doubleValue() * this.quantity;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}