package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Message;
import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.User;

import java.util.List;

public interface MessageService {
    void sendMessage(String message, User user, Property property);

    List<Message> getMessages(User actualUser);

    void deleteMessageById(Long messageId);
}
