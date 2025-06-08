package com.hotproperties.hotproperties.controller;

import com.hotproperties.hotproperties.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PropertyController {

    private final PropertyService propertyService;

    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping("/properties/list")
    public String allProperties (Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("properties", propertyService.findAllProperties());
            return "properties";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading properties: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }


}
