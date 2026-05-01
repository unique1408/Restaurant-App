package com.example.restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping({"/", "/index"})
    public String home(Model model) {
        model.addAttribute("pageTitle", "Welcome to Our Restaurant");
        model.addAttribute("welcomeMessage", "Delicious Food Delivered to Your Doorstep");
        return "index";
    }
    
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About Us");
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact Us");
        return "contact";
    }
}