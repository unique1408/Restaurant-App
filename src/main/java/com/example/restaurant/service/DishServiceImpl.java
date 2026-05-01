package com.example.restaurant.service;

import com.example.restaurant.model.Dish;
import com.example.restaurant.repository.CartItemRepository;
import com.example.restaurant.repository.DishRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    
    @Autowired
    private DishRepository dishRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Override
    public Dish addDish(Dish dish) {
        return dishRepository.save(dish);
    }
    
    @Override
    public Dish updateDish(Dish dish) {
        return dishRepository.save(dish);
    }
    
    @Transactional
    @Override
    public void deleteDish(Long id) {
    	cartItemRepository.deleteByDishId(id); // delete cart items first
        dishRepository.deleteById(id);  
    }
    
    @Override
    public Dish findById(Long id) {
        return dishRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));
    }
    
    @Override
    public List<Dish> findAllDishes() {
        return dishRepository.findAll();
    }
    
    @Override
    public List<Dish> findByCategory(String category) {
        return dishRepository.findByCategory(category);
    }
    
    @Override
    public List<Dish> findAvailableDishes() {
        return dishRepository.findByAvailable(true);
    }
    
    @Override
    public List<Dish> findByCategoryAndAvailable(String category, boolean available) {
        return dishRepository.findByCategoryAndAvailable(category, available);
    }
    
    @Override
    public Dish updateAvailability(Long id, boolean available) {
        Dish dish = findById(id);
        dish.setAvailable(available);
        return dishRepository.save(dish);
    }
}