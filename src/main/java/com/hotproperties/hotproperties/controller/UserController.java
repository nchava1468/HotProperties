package com.hotproperties.hotproperties.controller;

import com.hotproperties.hotproperties.dtos.ApiExceptionDto;
import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.exceptions.InvalidPropertyImageParameterException;
import com.hotproperties.hotproperties.exceptions.InvalidPropertyParameterException;
import com.hotproperties.hotproperties.exceptions.InvalidUserParameterException;
import com.hotproperties.hotproperties.service.PropertyService;
import com.hotproperties.hotproperties.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

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
        log.info("User accessing dashboard");
        try {
            userService.prepareDashboardModel(model);
            return "dashboard";
        } catch (Exception e) {
            log.error("Error in dashboard: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/users/profile")
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    public String profile(Model model, RedirectAttributes redirectAttributes) {
        log.info("User accessing profile");
        try {
            userService.prepareProfileModel(model);
            return "profile";
        } catch (Exception e) {
            log.error("Error in profile: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error loading profile: " + e.getMessage());
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/users/profile/edit")
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    public String editProfile(Model model) {
        log.info("User accessing edit profile page");
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
        log.info("User {} attempting to update profile", updatedUser.getEmail());
        try {
            User actualUser = userService.getCurrentUser();
            actualUser.setFirstName(updatedUser.getFirstName());
            actualUser.setLastName(updatedUser.getLastName());
            actualUser.setEmail(updatedUser.getEmail());

            userService.updateUser(actualUser);
            log.info("Profile updated successfully for user: {}", actualUser.getEmail());

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/users/profile";
        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
            return "redirect:/users/profile/edit";
        }
    }

    // === ADMIN ===

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewAllUsers(Model model, RedirectAttributes redirectAttributes) {
        log.info("Admin accessing all users view");
        try {
            model.addAttribute("users", userService.getAllUsers());
            return "users";
        } catch (Exception e) {
            log.error("Error loading users: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error loading users: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/admin/delete-user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        log.info("Admin attempting to delete user with ID: {}", id);
        try {
            userService.deleteUserById(id);
            log.info("User deleted successfully with ID: {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/create-agent")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateAgentForm(Model model) {
        log.info("Admin accessing create agent form");
        model.addAttribute("agent", new User());
        return "create-agent";
    }

    @PostMapping("/admin/create-agent")
    @PreAuthorize("hasRole('ADMIN')")
    public String createAgent(@ModelAttribute("agent") User agent,
                              RedirectAttributes redirectAttributes) {
        log.info("Admin attempting to create new agent: {}", agent.getEmail());
        try {
            userService.registerNewUser(agent, List.of("ROLE_AGENT"));
            log.info("Agent created successfully: {}", agent.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Agent created successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            log.error("Error creating agent: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error creating agent: " + e.getMessage());
            return "redirect:/admin/create-agent";
        }
    }

    // === AGENT ===

    @GetMapping("/agent/listings")
    @PreAuthorize("hasRole('AGENT')")
    public String showAgentListings(Model model) {
        log.info("Agent accessing property listings");
        List<Property> properties = propertyService.findAllProperties();
        model.addAttribute("properties", properties);
        return "manage-properties";
    }

    @GetMapping("/agent/add-property")
    @PreAuthorize("hasRole('AGENT')")
    public String showAddPropertyForm() {
        log.info("Agent accessing add property form");
        return "add-property";
    }

    @ExceptionHandler(InvalidUserParameterException.class)
    public ResponseEntity<?> handleInvalidUserParameterException (InvalidUserParameterException ex, HttpServletRequest request) {
        log.error("Invalid user parameter: {}", ex.getMessage());
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(apiExceptionDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidPropertyParameterException.class)
    public ResponseEntity<?> handleInvalidPropertyParameterException (InvalidPropertyParameterException ex, HttpServletRequest request) {
        log.error("Invalid property parameter: {}", ex.getMessage());
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(apiExceptionDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleNotFoundException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage());
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(apiExceptionDto, HttpStatus.BAD_REQUEST);
    }
}