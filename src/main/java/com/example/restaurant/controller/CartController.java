package com.example.restaurant.controller;

import com.example.restaurant.model.Cart;
import com.example.restaurant.model.CartItem;
import com.example.restaurant.model.User;
import com.example.restaurant.service.CartService;
import com.example.restaurant.service.DishService;
import com.example.restaurant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DishService dishService;
    
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userService.findByUsername(username).orElse(null);
        }
        return null;
    }
    
    @GetMapping
    public String viewCart(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Cart cart = cartService.getCartByUser(currentUser);
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("totalPrice", cart.getTotalPrice());
        
        return "cart";
    }
    
    @PostMapping("/add/{dishId}")
    public String addToCart(@PathVariable Long dishId,
                           @RequestParam(defaultValue = "1") int quantity,
                           RedirectAttributes redirectAttributes) {
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        try {
            cartService.addItemToCart(currentUser.getId(), dishId, quantity);
            redirectAttributes.addFlashAttribute("success", "Item added to cart successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding item to cart: " + e.getMessage());
        }
        
        return "redirect:/menu";
    }
    
    @PostMapping("/update/{itemId}")
    public String updateCartItem(@PathVariable Long itemId,
                                @RequestParam int quantity,
                                RedirectAttributes redirectAttributes) {
        
        try {
            if (quantity <= 0) {
                cartService.removeItemFromCart(itemId);
                redirectAttributes.addFlashAttribute("success", "Item removed from cart!");
            } else {
                cartService.updateCartItemQuantity(itemId, quantity);
                redirectAttributes.addFlashAttribute("success", "Cart updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating cart: " + e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    @GetMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable Long itemId,
                                RedirectAttributes redirectAttributes) {
        
        try {
            cartService.removeItemFromCart(itemId);
            redirectAttributes.addFlashAttribute("success", "Item removed from cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing item: " + e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    @GetMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        try {
            cartService.clearCart(currentUser.getId());
            redirectAttributes.addFlashAttribute("success", "Cart cleared successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error clearing cart: " + e.getMessage());
        }
        
        return "redirect:/cart";
    }
}