package com.hotproperties.hotproperties.controller;

import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.service.AuthService;
import com.hotproperties.hotproperties.service.UserService;
import com.hotproperties.hotproperties.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService, UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping({"/", "/index"})
    public String showIndex() {
        return "index";
    }

    // === LOGIN ===

    @GetMapping("/auth/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/auth/login")
    public String processLogin(@ModelAttribute("user") User user,
                               HttpServletResponse response,
                               Model model) {
        try {
            Cookie jwtCookie = authService.loginAndCreateJwtCookie(user);
            response.addCookie(jwtCookie);
            return "redirect:/dashboard";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    // === REGISTRATION ===

    @GetMapping("/auth/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/auth/register")
    public String registerUser(@ModelAttribute("user") User user,
                               @RequestParam("selectedRoles") List<String> roleNames,
                               RedirectAttributes redirectAttributes) {
        try {
            // First, register the user (this will assign them an ID)
            User savedUser = userService.registerNewUser(user, roleNames);

            redirectAttributes.addFlashAttribute("successMessage", "Registration successful.");
            return "redirect:/auth/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "redirect:/auth/register";
        }
    }

    // === LOGOUT ===

    @PostMapping("/auth/logout")
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    public String logout(HttpServletResponse response) {
        authService.clearJwtCookie(response);
        return "redirect:/auth/login";
    }
}