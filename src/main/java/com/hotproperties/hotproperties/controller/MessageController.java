package com.hotproperties.hotproperties.controller;

import com.hotproperties.hotproperties.entity.Message;
import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.service.MessageService;
import com.hotproperties.hotproperties.service.PropertyService;
import com.hotproperties.hotproperties.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import java.util.List;


@Controller
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final PropertyService propertyService;

    @Autowired
    public MessageController(MessageService messageService, UserService userService, PropertyService propertyService) {
        this.messageService = messageService;
        this.userService = userService;
        this.propertyService = propertyService;
    }

    @PostMapping("/messages/send")
    @PreAuthorize("hasRole('BUYER')")
    public String sendMessage(@RequestParam("message") String message,
                              @RequestParam("propertyId") Long propertyId,
                              RedirectAttributes redirectAttributes) {
        try {

            User user = userService.getCurrentUser();
            Property property = propertyService.findById(propertyId);

            messageService.sendMessage(message, user, property);

            redirectAttributes.addFlashAttribute("successMessage", "Message has been sent!");
            return "redirect:/properties/view/" + propertyId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send message: " + e.getMessage());
            return "redirect:/properties/view/" + propertyId;
        }
    }

    @GetMapping("/messages/buyer")
    @PreAuthorize("hasRole('BUYER')")
    public String viewBuyerMessages (Model model, RedirectAttributes redirectAttributes) {
        try {
            User actualUser = userService.getCurrentUser();
            List<Message> messageList = messageService.getMessages(actualUser);
            model.addAttribute("messageList", messageList);
            return "user-messages";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading favorites: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

}
