package com.hotproperties.hotproperties.controller;

import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        return "register";
    }
    @GetMapping("/registration-success")
    public String showSuccessPage() {
        return "registration-success";
    }
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String role,
                               Model model) {
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("errorMessage", "Username already exists.");
            return "register";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(role.toUpperCase()); // Assumes role column exists in entity
        userRepository.save(newUser);

        model.addAttribute("successMessage", "Registration successful!");
        return "redirect:/registration-success";
    }

}
