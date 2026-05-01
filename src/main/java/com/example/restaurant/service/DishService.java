package com.example.restaurant.service;

import com.example.restaurant.model.Dish;
import java.util.List;

public interface DishService {
    Dish addDish(Dish dish);
    Dish updateDish(Dish dish);
    void deleteDish(Long id);
    Dish findById(Long id);
    List<Dish> findAllDishes();
    List<Dish> findByCategory(String category);
    List<Dish> findAvailableDishes();
    List<Dish> findByCategoryAndAvailable(String category, boolean available);
    Dish updateAvailability(Long id, boolean available);
}