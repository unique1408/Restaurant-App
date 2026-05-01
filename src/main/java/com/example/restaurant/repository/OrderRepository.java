package com.example.restaurant.repository;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.User;
import com.example.restaurant.model.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
}