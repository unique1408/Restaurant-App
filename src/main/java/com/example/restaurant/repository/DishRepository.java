package com.example.restaurant.repository;

import com.example.restaurant.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByCategory(String category);
    List<Dish> findByAvailable(boolean available);
    List<Dish> findByCategoryAndAvailable(String category, boolean available);
}