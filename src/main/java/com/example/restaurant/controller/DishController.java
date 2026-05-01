package com.example.restaurant.controller;

import com.example.restaurant.model.Dish;
import com.example.restaurant.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/menu")
public class DishController {
    
    @Autowired
    private DishService dishService;
    
    @GetMapping
    public String showMenu(Model model) {
        List<Dish> dishes = dishService.findAvailableDishes();
        List<String> categories = dishes.stream()
            .map(Dish::getCategory)
            .distinct()
            .toList();
        
        model.addAttribute("dishes", dishes);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", "all");
        
        return "menu";
    }
    
    @GetMapping("/category/{category}")
    public String showMenuByCategory(@PathVariable String category, Model model) {
        List<Dish> dishes;
        
        if ("all".equals(category)) {
            dishes = dishService.findAvailableDishes();
        } else {
            dishes = dishService.findByCategoryAndAvailable(category, true);
        }
        
        List<String> categories = dishService.findAllDishes().stream()
            .map(Dish::getCategory)
            .distinct()
            .toList();
        
        model.addAttribute("dishes", dishes);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        
        return "menu";
    }
    
    @GetMapping("/dish/{id}")
    public String viewDishDetails(@PathVariable Long id, Model model) {

        Dish dish = dishService.findById(id);

        if(dish == null){
            return "error";
        }

        model.addAttribute("dish", dish);
        return "dish-details";
    }
    
    @GetMapping("/search")
    public String searchDishes(@RequestParam String keyword, Model model) {
        List<Dish> allDishes = dishService.findAllDishes();
        List<Dish> searchResults = allDishes.stream()
            .filter(dish -> dish.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                           dish.getDescription().toLowerCase().contains(keyword.toLowerCase()))
            .filter(Dish::isAvailable)
            .toList();
        
        List<String> categories = dishService.findAllDishes().stream()
            .map(Dish::getCategory)
            .distinct()
            .toList();
        
        model.addAttribute("dishes", searchResults);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", "all");
        model.addAttribute("searchKeyword", keyword);
        
        return "menu";
    }
}