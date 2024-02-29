package com.company.chatapp.repository;

import com.company.chatapp.domain.Message;
import com.company.chatapp.domain.Room;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Message entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    public List<Message> findAllByRoom(Room room);
}
