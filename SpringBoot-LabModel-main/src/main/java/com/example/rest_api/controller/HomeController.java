package com.example.rest_api.controller;

import com.example.rest_api.database.users.repository.UserRepository;
import com.example.rest_api.security.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("user")
public class HomeController {
    UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        if (principal instanceof AuthenticatedUser authenticatedUser) {
            // Since AuthenticatedUser contains email and other attributes
            model.addAttribute("username", authenticatedUser.getEmail());
        } else {
            // Fallback if principal is not AuthenticatedUser for some reason
            model.addAttribute("username", principal.getName());
        }
        return "user/home";
    }



}

