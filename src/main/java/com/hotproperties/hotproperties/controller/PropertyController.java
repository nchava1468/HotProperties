package com.hotproperties.hotproperties.controller;

import com.hotproperties.hotproperties.dtos.ApiExceptionDto;
import com.hotproperties.hotproperties.entity.Favorite;
import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.exceptions.InvalidFavoriteParameterException;
import com.hotproperties.hotproperties.exceptions.InvalidPropertyImageParameterException;
import com.hotproperties.hotproperties.exceptions.InvalidPropertyParameterException;
import com.hotproperties.hotproperties.service.FavoriteService;
import com.hotproperties.hotproperties.service.PropertyService;
import com.hotproperties.hotproperties.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class PropertyController {
    private static final Logger log = LoggerFactory.getLogger(PropertyController.class);

    private final PropertyService propertyService;
    private final UserService userService;
    private final FavoriteService favoriteService;

    @Autowired
    public PropertyController(PropertyService propertyService, UserService userService, FavoriteService favoriteService) {
        this.propertyService = propertyService;
        this.userService = userService;
        this.favoriteService = favoriteService;
    }

    @GetMapping("/properties/list")
    @PreAuthorize("hasRole('BUYER')")
    public String allProperties (Model model, RedirectAttributes redirectAttributes) {
        log.info("User accessing properties list");
        try {
            model.addAttribute("properties", propertyService.findAllProperties());
            return "properties";
        } catch (Exception e) {
            log.error("Error loading properties: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error loading properties: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/properties/search")
    @PreAuthorize("hasRole('BUYER')")
    public String searchProperties(
            @RequestParam(required = false) Integer zip,
            @RequestParam(required = false) Integer minSqFt,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String sort,
            Model model) {
        log.info("User searching properties with filters - zip: {}, minSqFt: {}, minPrice: {}, maxPrice: {}, sort: {}",
                zip, minSqFt, minPrice, maxPrice, sort);

        List<Property> properties = propertyService.filterProperties(zip, minSqFt, minPrice, maxPrice, sort);
        model.addAttribute("properties", properties);
        return "properties";
    }

    @GetMapping("/properties/view/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public String viewProperty (@PathVariable Long id,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        log.info("User viewing property with ID: {}", id);
        try {
            Property property = propertyService.findById(id);
            User actualUser = userService.getCurrentUser();
            boolean favoriteExists = favoriteService.favoriteExists(property, actualUser);

            model.addAttribute("favoriteExists", favoriteExists);
            model.addAttribute("property", property);
            return "property";
        } catch (Exception e) {
            log.error("Error viewing property: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error loading properties: " + e.getMessage());
            return "redirect:/properties/list";
        }
    }

    @GetMapping("/properties/edit/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.info("Agent accessing edit form for property: {}", id);
        model.addAttribute("property", propertyService.findById(id));
        return "edit-property";
    }

    @PostMapping("/properties/edit/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public String updateProperty(@PathVariable Long id, @ModelAttribute Property property,
                                 @RequestParam(value = "images", required = false) MultipartFile[] images,
                                 RedirectAttributes redirectAttributes) {
        log.info("Agent updating property: {}", id);
        try {
            propertyService.updateProperty(id, property, images);
            log.info("Property {} updated successfully", id);
            redirectAttributes.addFlashAttribute("successMessage", "Property updated successfully");
            return "redirect:/agent/listings";
        } catch (Exception e) {
            log.error("Error updating property: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting property: " + e.getMessage());
        }
        return "redirect:/agent/listings";
    }

    @PostMapping("/properties/{propertyId}/images/{imageId}/delete")
    @PreAuthorize("hasRole('AGENT')")
    public String deleteImage(@PathVariable Long propertyId, @PathVariable Long imageId) {
        log.info("Agent deleting image {} from property {}", imageId, propertyId);
        propertyService.deleteImage(propertyId, imageId);
        return "redirect:/properties/edit/" + propertyId;
    }

    @PostMapping("/agent/delete-property/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public String deleteProperty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Agent deleting property: {}", id);
        try {
            propertyService.deletePropertyById(id);
            log.info("Property {} deleted successfully", id);
            redirectAttributes.addFlashAttribute("successMessage", "Property deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting property: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting property: " + e.getMessage());
        }
        return "redirect:/agent/listings";
    }

    @PostMapping("/favorites/add/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public String addFavorite(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        log.info("User adding property {} to favorites", id);
        try {
            User actualUser = userService.getCurrentUser();
            Property property = propertyService.findById(id);

            favoriteService.addFavorite(property, actualUser);
            log.info("Property {} added to favorites for user {}", id, actualUser.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Added Property to Favorites!");
            return "redirect:/properties/view/" + id;
        } catch (Exception e) {
            log.error("Error adding to favorites: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to add to Favorites: " + e.getMessage());
            return "redirect:/properties/list";
        }
    }

    @GetMapping("/properties/favorites")
    @PreAuthorize("hasRole('BUYER')")
    public String viewFavorites (Model model, RedirectAttributes redirectAttributes) {
        log.info("User accessing favorites list");
        try {
            User actualUser = userService.getCurrentUser();
            List<Favorite> favoriteList = favoriteService.getFavorites(actualUser);
            model.addAttribute("favoriteList", favoriteList);
            return "favorites";
        } catch (Exception e) {
            log.error("Error loading favorites: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error loading favorites: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/properties/add")
    @PreAuthorize("hasRole('AGENT')")
    public String addProperty(
            @RequestParam String title,
            @RequestParam double price,
            @RequestParam String location,
            @RequestParam String size,
            @RequestParam(required = false) String description,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            RedirectAttributes redirectAttributes) {
        log.info("Agent adding new property: {}", title);
        Integer sizeInt = Integer.valueOf(size);
        Property property = new Property(title, price, description, location, sizeInt);
        propertyService.addNewProperty(property, images);
        log.info("Property {} added successfully", property.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Property added successfully!");
        return "redirect:/agent/listings";
    }

    @PostMapping("/favorites/delete/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public String deleteFavorite(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        log.info("User removing property {} from favorites", id);
        try {
            Property property = propertyService.findById(id);
            User actualUser = userService.getCurrentUser();

            favoriteService.deleteFavorite(property, actualUser);
            log.info("Property {} removed from favorites for user {}", id, actualUser.getEmail());

            redirectAttributes.addFlashAttribute("successMessage", "Removed Property from Favorites.");
            return "redirect:/properties/view/" + id;
        } catch (Exception e) {
            log.error("Error removing from favorites: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error removing from favorites: " + e.getMessage());
            return "redirect:/properties/list";
        }
    }

    @GetMapping("/property-images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> servePropertyImage(@PathVariable String filename) {
        log.info("Serving property image: {}", filename);
        try {
            Path filePath = Paths.get("uploads/property-images/").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                        .body(resource);
            } else {
                log.warn("Image {} not found or not readable", filename);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error serving image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

    @ExceptionHandler(InvalidFavoriteParameterException.class)
    public ResponseEntity<?> handleInvalidFavoriteParameterException (InvalidFavoriteParameterException ex, HttpServletRequest request) {
        log.error("Invalid favorite parameter: {}", ex.getMessage());
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(apiExceptionDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidPropertyImageParameterException.class)
    public ResponseEntity<?> handleInvalidPropertyImageParameterException (InvalidPropertyImageParameterException ex, HttpServletRequest request) {
        log.error("Invalid property image parameter: {}", ex.getMessage());
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