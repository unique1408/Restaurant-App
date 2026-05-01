package com.example.restaurant.controller;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.Order;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.OrderRepository;
import com.example.restaurant.service.DishService;
import com.example.restaurant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private DishService dishService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    private static String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/images/";
    
   
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long dishCount = dishService.findAllDishes().size();
        long userCount = userService.findAllUsers().size();
        long availableDishes = dishService.findAvailableDishes().size();
        
        model.addAttribute("dishCount", dishCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("availableDishes", availableDishes);
        
        return "admin/dashboard";
    }
    
    @GetMapping("/dishes")
    public String manageDishes(Model model) {
        List<Dish> dishes = dishService.findAllDishes();
        model.addAttribute("dishes", dishes);
        return "admin/manage-dishes";
    }
    
    @GetMapping("/dishes/add")
    public String showAddDishForm(Model model) {
        model.addAttribute("dish", new Dish());
        return "admin/add-dish";
    }
    
    @PostMapping("/dishes/add")
    public String addDish(@ModelAttribute Dish dish,
                         @RequestParam("imageFile") MultipartFile imageFile,
                         RedirectAttributes redirectAttributes) {
        
        try {
            if (!imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, imageFile.getBytes());
                dish.setImagePath("/images/" + fileName);
            }
            
            dishService.addDish(dish);
            redirectAttributes.addFlashAttribute("success", "Dish added successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error uploading image: " + e.getMessage());
        }
        
        return "redirect:/admin/dishes";
    }
    
    @GetMapping("/dishes/edit/{id}")
    public String showEditDishForm(@PathVariable Long id, Model model) {
        Dish dish = dishService.findById(id);
        model.addAttribute("dish", dish);
        return "admin/edit-dish";
    }
    
    @PostMapping("/dishes/update")
    public String updateDish(@ModelAttribute Dish dish,
                            @RequestParam("imageFile") MultipartFile imageFile,
                            RedirectAttributes redirectAttributes) {
        
        try {
            if (!imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, imageFile.getBytes());
                dish.setImagePath("/images/" + fileName);
            }
            
            dishService.updateDish(dish);
            redirectAttributes.addFlashAttribute("success", "Dish updated successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error updating image: " + e.getMessage());
        }
        
        return "redirect:/admin/dishes";
    }
    
    @GetMapping("/dishes/delete/{id}")
    public String deleteDish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dishService.deleteDish(id);
            redirectAttributes.addFlashAttribute("success", "Dish deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting dish: " + e.getMessage());
        }
        return "redirect:/admin/dishes";
    }
    
    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/manage-users";
    }
    
    
    @PostMapping("/dishes/{id}/toggle-availability")
    @ResponseBody
    public void toggleAvailability(@PathVariable Long id) {

        Dish dish = dishService.findById(id);

        if (dish != null) {
            dish.setAvailable(!dish.isAvailable());
            dishService.updateDish(dish); // use your service update
        }
    }
    
    
    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {

        Order order = orderRepository.findById(id).orElse(null);

        model.addAttribute("order", order);

        return "admin/order-details";
    }
    
    
    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam Long orderId,
                                    @RequestParam String status) {

        Order order = orderRepository.findById(orderId).orElse(null);

        if (order != null) {

            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);

            order.setStatus(orderStatus);
            orderRepository.save(order);
        }

        return "redirect:/admin/orders/" + orderId;
    }
    
    @GetMapping("/all-orders")
    public String showAllOrders(Model model) {

        List<Order> orders = orderRepository.findAll();

        model.addAttribute("orders", orders);

        return "admin/all-orders";
    }
    
    @GetMapping("/reports")
    public String showReports(Model model) {

        long totalOrders = orderRepository.count();

        double totalRevenue = orderRepository.findAll()
                .stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        long totalUsers = userService.findAllUsers().size();

        long availableDishes = dishService.findAvailableDishes().size();

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("availableDishes", availableDishes);

        return "admin/reports";
    }
    
    
    @GetMapping("/api/admin/recent-orders")
    @ResponseBody
    public List<Order> getRecentOrders() {
        
        List<Order> orders = orderRepository.findAll();

        // return last 5 orders
        return orders.stream()
                .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
                .limit(5)
                .toList();
    }
    
    
    @GetMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            user.setEnabled(!user.isEnabled());
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("success", "User status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}