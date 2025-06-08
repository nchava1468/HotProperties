package com.hotproperties.hotproperties.controller;

import com.hotproperties.hotproperties.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.hotproperties.hotproperties.entity.Property;

@Controller
public class PropertyController {

    private final PropertyService propertyService;

    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
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
                                Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("property", propertyService.findById(id));
            return "property";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading properties: " + e.getMessage());
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

}
