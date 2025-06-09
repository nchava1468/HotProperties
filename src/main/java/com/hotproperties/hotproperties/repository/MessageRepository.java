package com.hotproperties.hotproperties.repository;

import com.hotproperties.hotproperties.entity.Message;
import com.hotproperties.hotproperties.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.id = :id")
    void deleteMessageById(@Param("id") Long id);
}
