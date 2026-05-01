package com.example.restaurant.controller;

import com.example.restaurant.model.Order;
import com.example.restaurant.repository.OrderRepository;
import com.example.restaurant.repository.DishRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DishRepository dishRepository;

    // Recent Orders
    @GetMapping("/recent-orders")
    public List<Map<String, Object>> getRecentOrders() {

        List<Order> orders = orderRepository.findAll();

        List<Map<String, Object>> result = new ArrayList<>();

        orders.stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(5)
                .forEach(order -> {

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", order.getId());
                    map.put("userName", order.getUser().getUsername());
                    map.put("itemCount", order.getItems().size());
                    map.put("totalAmount", order.getTotalAmount());
                    map.put("status", order.getStatus());
                    map.put("orderDate", order.getOrderDate());

                    result.add(map);
                });

        return result;
    }

    // Dashboard Stats
    @GetMapping("/dashboard-stats")
    public Map<String, Object> getStats() {

        Map<String, Object> stats = new HashMap<>();

        List<Order> orders = orderRepository.findAll();

        long pending = orders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.PENDING)
                .count();

        double todayRevenue = orders.stream()
                .filter(o -> o.getOrderDate().toLocalDate().equals(LocalDate.now()))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        stats.put("pendingOrders", pending);
        stats.put("todayRevenue", todayRevenue);
        stats.put("avgRating", 4.5);

        return stats;
    }

    // Popular Dishes (simple version)
    @GetMapping("/popular-dishes")
    public List<Map<String, Object>> getPopularDishes() {

        List<Map<String, Object>> dishes = new ArrayList<>();

        dishRepository.findAll().stream().limit(5).forEach(dish -> {

            Map<String, Object> map = new HashMap<>();
            map.put("name", dish.getName());
            map.put("imagePath", dish.getImagePath());
            map.put("orderCount", new Random().nextInt(50));

            dishes.add(map);
        });

        return dishes;
    }
}