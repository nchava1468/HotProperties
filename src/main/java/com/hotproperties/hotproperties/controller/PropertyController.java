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
        try {
            model.addAttribute("properties", propertyService.findAllProperties());
            return "properties";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading properties: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/properties/view/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public String viewProperty (@PathVariable Long id,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            Property property = propertyService.findById(id);
            User actualUser = userService.getCurrentUser();
            boolean favoriteExists = favoriteService.favoriteExists(property, actualUser);

            model.addAttribute("favoriteExists", favoriteExists);
            model.addAttribute("property", property);
            return "property";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading properties: " + e.getMessage());
            return "redirect:/properties/list";
        }
    }

    @GetMapping("/properties/edit/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("property", propertyService.findById(id));
        return "edit-property";
    }

    @PostMapping("/properties/edit/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public String updateProperty(@PathVariable Long id, @ModelAttribute Property property, @RequestParam(value = "images", required = false) MultipartFile[] images) {
        propertyService.updateProperty(id, property, images);
        return "redirect:/agent/listings";
    }

    @PostMapping("/properties/{propertyId}/images/{imageId}/delete")
    @PreAuthorize("hasRole('AGENT')")
    public String deleteImage(@PathVariable Long propertyId, @PathVariable Long imageId) {
        propertyService.deleteImage(propertyId, imageId);
        return "redirect:/properties/edit/" + propertyId;
    }

    @PostMapping("/agent/delete-property/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public String deleteProperty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            propertyService.deletePropertyById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Property deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting property: " + e.getMessage());
        }
        return "redirect:/agent/listings";
    }

    @PostMapping("/favorites/add/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public String addFavorite(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            User actualUser = userService.getCurrentUser();
            Property property = propertyService.findById(id);

            favoriteService.addFavorite(property, actualUser);
            redirectAttributes.addFlashAttribute("successMessage", "Added Property to Favorites!");
            return "redirect:/properties/view/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add to Favorites: " + e.getMessage());
            return "redirect:/properties/list";
        }
    }

    @GetMapping("/properties/favorites")
    @PreAuthorize("hasRole('BUYER')")
    public String viewFavorites (Model model, RedirectAttributes redirectAttributes) {
        try {
            User actualUser = userService.getCurrentUser();
            List<Favorite> favoriteList = favoriteService.getFavorites(actualUser);
            model.addAttribute("favoriteList", favoriteList);
            return "favorites";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading favorites: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/favorites/delete/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public String deleteFavorite(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            Property property = propertyService.findById(id);
            User actualUser = userService.getCurrentUser();

            favoriteService.deleteFavorite(property, actualUser);

            redirectAttributes.addFlashAttribute("successMessage", "Removed Property from Favorites.");
            return "redirect:/properties/view/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing from favorites: " + e.getMessage());
            return "redirect:/properties/list";
        }
    }


    @GetMapping("/property-images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> servePropertyImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/property-images/").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ExceptionHandler(InvalidPropertyParameterException.class)
    public ResponseEntity<?> handleInvalidPropertyParameterException (InvalidPropertyParameterException ex, HttpServletRequest request) {
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
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(apiExceptionDto, HttpStatus.BAD_REQUEST);
    }
}
