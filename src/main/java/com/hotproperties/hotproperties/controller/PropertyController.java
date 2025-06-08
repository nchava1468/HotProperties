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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;

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

}
