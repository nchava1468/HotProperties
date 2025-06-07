package com.hotproperties.hotproperties.controller;

import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.service.AuthService;
import com.hotproperties.hotproperties.service.UserService;
import com.hotproperties.hotproperties.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/users/profile")
    public String profile(Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.prepareProfileModel(model);
            return "profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading profile: " + e.getMessage());
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/users/profile/edit")
    public String editProfile(Model model) {
        model.addAttribute("user", new User());
        return "edit-profile";
    }

    @PostMapping("/users/profile/edit")
    public String saveEditProfile(@ModelAttribute("user") User updatedUser,
                                  @RequestParam(required = false) String firstName,
                                  @RequestParam(required = false) String lastName,
                                  @RequestParam(required = false) String email,
                                  RedirectAttributes redirectAttributes) {
        try {
            User actualUser = userService.getCurrentUser();

            actualUser.setFirstName(updatedUser.getFirstName());
            actualUser.setLastName(updatedUser.getLastName());
            actualUser.setEmail(updatedUser.getEmail());

            userService.updateUser(actualUser);

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/users/profile";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/profile/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
            return "redirect:/users/profile/edit";
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
