package com.hotproperties.hotproperties.repository;

import com.hotproperties.hotproperties.entity.Message;
import com.hotproperties.hotproperties.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByUser(User user);
}
