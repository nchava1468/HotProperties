package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Message;
import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.exceptions.InvalidMessageParameterException;
import com.hotproperties.hotproperties.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final PropertyService propertyService;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserService userService, PropertyService propertyService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.propertyService = propertyService;
    }

    @Override
    public void sendMessage(String message, User user, Property property) {

        validateContent(message);
        validateUser(user);
        validateProperty(property);

        Message newMessage = new Message();

        newMessage.setContent(message);
        newMessage.setUser(user);
        newMessage.setProperty(property);
        newMessage.setTimestamp(LocalDateTime.now());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        newMessage.setTimeString(newMessage.getTimestamp().format(dateTimeFormatter));

        user.addMessage(newMessage);
        property.addMessage(newMessage);

        messageRepository.save(newMessage);
    }

    @Override
    public List<Message> getMessages(User user) {
        return messageRepository.findByUser(user);
    }

    // === VALIDATION METHODS ===

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidMessageParameterException("Title is required");
        }
    }

    private void validateUser(User user) {
        if (userService.findById(user.getId()) == null) {
            throw new InvalidMessageParameterException("User not found.");
        }
    }

    private void validateProperty(Property property) {
        if (propertyService.findById(property.getId()) == null) {
            throw new InvalidMessageParameterException("Property not found.");
        }
    }

}
