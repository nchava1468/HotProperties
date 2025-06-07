package com.hotproperties.hotproperties.controller;

import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.service.AuthService;
import com.hotproperties.hotproperties.service.UserService;
import com.hotproperties.hotproperties.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(AuthService authService, UserService userService, UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.prepareDashboardModel(model);
            return "dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // === ADMIN ===

    @GetMapping("/admin/users")
    public String viewAllUsers(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("users", userService.getAllUsers());
            return "users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading users: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/admin/delete-user/{id}")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/create-agent")
    public String showCreateAgentForm(Model model) {
        model.addAttribute("agent", new User());
        return "create-agent";
    }
/*
    @PostMapping("/admin/create-agent")
    public String createAgent(@ModelAttribute("agent") User agent,
                              RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (agent.getUsername() == null || agent.getUsername().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Username is required");
                return "redirect:/admin/create-agent";
            }

            if (agent.getPassword() == null || agent.getPassword().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Password is required");
                return "redirect:/admin/create-agent";
            }

            if (agent.getEmail() == null || agent.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email is required");
                return "redirect:/admin/create-agent";
            }

            if (agent.getFirstName() == null || agent.getFirstName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "First name is required");
                return "redirect:/admin/create-agent";
            }

            if (agent.getLastName() == null || agent.getLastName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Last name is required");
                return "redirect:/admin/create-agent";
            }

            // Check if username already exists
            if (userService.existsByUsername(agent.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/admin/create-agent";
            }

            // Create the agent
            userService.registerNewUser(agent, java.util.List.of("ROLE_AGENT"));
            redirectAttributes.addFlashAttribute("success", "Agent created successfully!");
            return "redirect:/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating agent: " + e.getMessage());
            return "redirect:/admin/create-agent";
        }
    }*/
    @PostMapping("/admin/create-agent")
    public String createAgent(@ModelAttribute("agent") User agent,
                              RedirectAttributes redirectAttributes) {
        try {
            userService.registerNewUser(agent, List.of("ROLE_AGENT"));
            redirectAttributes.addFlashAttribute("success", "Agent created successfully!");
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/create-agent";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating agent: " + e.getMessage());
            return "redirect:/admin/create-agent";
        }
    }



}
