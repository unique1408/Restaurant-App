package com.example.restaurant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;
    
    private int quantity;
    
    private double price;
    
    private double subtotal;
    
    public OrderItem() {
    }
    
    public OrderItem(Order order, Dish dish, int quantity, double price) {
        this.order = order;
        this.dish = dish;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = price * quantity;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
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
        this.subtotal = this.price * this.quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
        this.subtotal = this.price * this.quantity;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}