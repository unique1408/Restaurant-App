package com.example.restaurant.repository;

import com.example.restaurant.model.CartItem;
import com.example.restaurant.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    void deleteByCart(Cart cart);
    
    void deleteByDishId(Long dishId);
}