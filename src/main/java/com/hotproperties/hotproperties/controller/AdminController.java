package com.hotproperties.hotproperties.controller;

import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, RedirectAttributes redirectAttributes) {
        try {
            // Get current authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Add user data to model
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("role", "ADMIN");
            model.addAttribute("username", username);
            
            return "admin-dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("users", userService.getAllUsers());
            return "users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading users: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/delete-user/{id}")
    public String deleteUser(@org.springframework.web.bind.annotation.PathVariable Long id, 
                           RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/create-agent")
    public String showCreateAgentForm(Model model) {
        model.addAttribute("agent", new User());
        return "create-agent";
    }

    @PostMapping("/create-agent")
    public String createAgent(@org.springframework.web.bind.annotation.ModelAttribute("agent") User agent,
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
            userService.registerNewUser(agent, java.util.List.of("ROLE_MANAGER"));
            redirectAttributes.addFlashAttribute("success", "Agent created successfully!");
            return "redirect:/admin/dashboard";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating agent: " + e.getMessage());
            return "redirect:/admin/create-agent";
        }
    }
}


