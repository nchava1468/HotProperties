package com.hotproperties.hotproperties.controller;

import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.service.PropertyService;
import com.hotproperties.hotproperties.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final PropertyService propertyService;

    @Autowired
    public UserController(UserService userService, PropertyService propertyService) {
        this.userService = userService;
        this.propertyService = propertyService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
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
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
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
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    public String editProfile(Model model) {
        model.addAttribute("user", new User());
        return "edit-profile";
    }

    @PostMapping("/users/profile/edit")
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/create-agent")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateAgentForm(Model model) {
        model.addAttribute("agent", new User());
        return "create-agent";
    }

    @PostMapping("/admin/create-agent")
    @PreAuthorize("hasRole('ADMIN')")
    public String createAgent(@ModelAttribute("agent") User agent,
                              RedirectAttributes redirectAttributes) {
        try {
            userService.registerNewUser(agent, List.of("ROLE_AGENT"));
            redirectAttributes.addFlashAttribute("successMessage", "Agent created successfully!");
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/create-agent";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating agent: " + e.getMessage());
            return "redirect:/admin/create-agent";
        }
    }

    // === AGENT ===

    @GetMapping("/agent/listings")
    @PreAuthorize("hasRole('AGENT')")
    public String showAgentListings(Model model) {
        List<Property> properties = propertyService.findAllProperties();
        model.addAttribute("properties", properties);
        return "manage-properties";
    }

    @GetMapping("/agent/add-property")
    @PreAuthorize("hasRole('AGENT')")
    public String showAddPropertyForm() {
        return "add-property";
    }

    @PostMapping("/agent/properties/add")
    @PreAuthorize("hasRole('AGENT')")
    public String addProperty(
            @RequestParam String title,
            @RequestParam double price,
            @RequestParam String location,
            @RequestParam String size,
            @RequestParam(required = false) String description,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            RedirectAttributes redirectAttributes
    ) {
        Integer sizeInt = Integer.valueOf(size);
        Property property = new Property(title, price, description, location, sizeInt);

        propertyService.addNewProperty(property, images);
        redirectAttributes.addFlashAttribute("success", "Property added successfully!");
        return "redirect:/agent/listings";
    }
}
