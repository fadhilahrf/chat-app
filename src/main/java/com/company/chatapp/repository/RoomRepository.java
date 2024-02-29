package com.company.chatapp.repository;

import com.company.chatapp.domain.Room;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Room entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoomRepository extends MongoRepository<Room, String> {

    @Query("{ $or: [{ user1: ?0 }, { user2: ?0 }] }")
    public List<Room> findAllByCurrentUser(String user);

    @Query("{ $or: [{ $and: [ { user1: ?0 }, { user2: ?1 } ] }, { $and: [ { user1: ?1 }, { user2: ?0 } ] }] }")
    public Optional<Room> findOneByUser1AndUser2(String user1, String user2);
}
