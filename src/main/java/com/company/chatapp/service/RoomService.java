package com.company.chatapp.service;

import com.company.chatapp.domain.Room;
import com.company.chatapp.repository.RoomRepository;
import com.company.chatapp.security.SecurityUtils;
import com.company.chatapp.service.dto.RoomDTO;
import com.company.chatapp.service.mapper.RoomMapper;

import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.company.chatapp.domain.Room}.
 */
@Service
public class RoomService {

    private final Logger log = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;

    private final RoomMapper roomMapper;

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
    }

    /**
     * Save a room.
     *
     * @param roomDTO the entity to save.
     * @return the persisted entity.
     */
    public RoomDTO save(RoomDTO roomDTO) {
        log.debug("Request to save Room : {}", roomDTO);
        Room room = roomMapper.toEntity(roomDTO);
        room = roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    /**
     * Update a room.
     *
     * @param roomDTO the entity to save.
     * @return the persisted entity.
     */
    public RoomDTO update(RoomDTO roomDTO) {
        log.debug("Request to update Room : {}", roomDTO);
        Room room = roomMapper.toEntity(roomDTO);
        room = roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    /**
     * Partially update a room.
     *
     * @param roomDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RoomDTO> partialUpdate(RoomDTO roomDTO) {
        log.debug("Request to partially update Room : {}", roomDTO);

        return roomRepository
            .findById(roomDTO.getId())
            .map(existingRoom -> {
                roomMapper.partialUpdate(existingRoom, roomDTO);

                return existingRoom;
            })
            .map(roomRepository::save)
            .map(roomMapper::toDto);
    }

    /**
     * Get all the rooms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<RoomDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Rooms");
        return roomRepository.findAll(pageable).map(roomMapper::toDto);
    }

    public List<RoomDTO> findAllByCurrentUser(String currentUser) {
        log.debug("Request to get all Rooms by user");
        return roomRepository.findAllByCurrentUser(currentUser).stream().map(roomMapper::toDto).toList();
    }

    public List<RoomDTO> findAllByCurrentUserSortedByLatestMessageTime(String currentUser) {
        log.debug("Request to get all Rooms sorted by latest message time by user");
        Sort sort = Sort.by(Sort.Direction.DESC, "latestMessageTime");
        return roomRepository.findAllByCurrentUserSortedBy(currentUser, sort).stream().map(roomMapper::toDto).toList();
    }

    public List<RoomDTO> findAllByCurrentUser() {
        try {
            String username = SecurityUtils.getCurrentUserLogin().orElseThrow(()-> new NotFoundException("Username not found"));

            return findAllByCurrentUser(username);
        } catch( Exception e ) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public List<RoomDTO> findAllByCurrentUserSortedByLatestMessageTime() {
        try {
            String username = SecurityUtils.getCurrentUserLogin().orElseThrow(()-> new NotFoundException("Username not found"));

            return findAllByCurrentUserSortedByLatestMessageTime(username);
        } catch( Exception e ) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Get one room by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<RoomDTO> findOne(String id) {
        log.debug("Request to get Room : {}", id);
        return roomRepository.findById(id).map(roomMapper::toDto);
    }

    public Optional<Room> findByUser1AndUser2(String user1, String user2) {
        log.debug("Request to get Room By user1 and user2 : {} and {}", user1, user2);
        return roomRepository.findOneByUser1AndUser2(user1, user2);
    }

    public Room findOrCreate(String user1, String user2) {
        log.debug("Request to get or create Room By user1 and user2 : {} and {}", user1, user2);
        Optional<Room> optionalRoom = roomRepository.findOneByUser1AndUser2(user1, user2);
        Room room = null;
        if(optionalRoom.isPresent()) {
            room = optionalRoom.get();
        }else {
            room = new Room();
            room.setUser1(user1);
            room.setUser2(user2);
            room = roomRepository.save(room);
        }

        return room;
    }

    /**
     * Delete the room by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Room : {}", id);
        roomRepository.deleteById(id);
    }
}
