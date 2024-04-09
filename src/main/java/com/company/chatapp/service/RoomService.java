package com.company.chatapp.service;

import com.company.chatapp.domain.Message;
import com.company.chatapp.domain.Room;
import com.company.chatapp.repository.MessageRepository;
import com.company.chatapp.repository.RoomRepository;
import com.company.chatapp.security.SecurityUtils;
import com.company.chatapp.service.dto.MessageDTO;
import com.company.chatapp.service.dto.RoomDTO;
import com.company.chatapp.service.mapper.RoomMapper;

import java.util.ArrayList;
import java.util.Arrays;
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

    private final MessageRepository messageRepository;

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper, MessageRepository messageRepository) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.messageRepository = messageRepository;
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
        return roomRepository.findAllByCurrentUserSortedBy(currentUser, sort).stream().filter(room->{
            List<String> roomDeleters = new ArrayList<>(Arrays.asList(room.getDeletedBy().split(";")));
            return !roomDeleters.contains(currentUser);
        }).map(room->{
            RoomDTO roomDTO = roomMapper.toDto(room);
            Optional<Message> messageOptional = messageRepository.findFirstByRoomIdAndDeletedByNotContainingOrderByDeliveryTimeDesc(room.getId(), currentUser);
            if(messageOptional.isPresent()) {
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setSender(messageOptional.get().getSender());
                messageDTO.setContent(messageOptional.get().getContent());
                messageDTO.setRead(messageOptional.get().getRead());
                roomDTO.setLatestMessage(messageDTO);
            }
            return roomDTO;
        }).toList();
    }

    public List<RoomDTO> findAllByCurrentUser() {
        Optional<String> usernameOptional = SecurityUtils.getCurrentUserLogin();
        if(usernameOptional.isPresent()) {
            return findAllByCurrentUser(usernameOptional.get());
        }

        return new ArrayList<>();
    }

    public List<RoomDTO> findAllByCurrentUserSortedByLatestMessageTime() {
        Optional<String> usernameOptional = SecurityUtils.getCurrentUserLogin();
        if(usernameOptional.isPresent()) {
            return findAllByCurrentUserSortedByLatestMessageTime(usernameOptional.get());
        }

        return new ArrayList<>();
    }

    /**
     * Get one room by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<RoomDTO> findOne(String id, String currentUser) {
        log.debug("Request to get Room : {}", id);
        return roomRepository.findById(id).map(room->{
            RoomDTO roomDTO = roomMapper.toDto(room);
            Optional<Message> messageOptional = messageRepository.findFirstByRoomIdAndDeletedByNotContainingOrderByDeliveryTimeDesc(room.getId(), currentUser);
            if(messageOptional.isPresent()) {
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setSender(messageOptional.get().getSender());
                messageDTO.setContent(messageOptional.get().getContent());
                messageDTO.setRead(messageOptional.get().getRead());
                roomDTO.setLatestMessage(messageDTO);
            }
            return roomDTO;
        });
    }

    public Optional<RoomDTO> findOne(String id) {
        Optional<String> usernameOptional = SecurityUtils.getCurrentUserLogin();
        if(usernameOptional.isPresent()) {
            return findOne(id, usernameOptional.get());
        }

        return Optional.empty();
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

    public Optional<RoomDTO> softDeleteForUser(String id, String username) {
        log.debug("Request to soft delete Room : {} for user : {}", id, username);
        Optional<Room> roomOptional = roomRepository.findById(id);

        if(roomOptional.isPresent()) {
            Room room = roomOptional.get();
            if(room.getUser1().equals(username) || room.getUser2().equals(username)) {
                List<String> roomDeleters = new ArrayList<>(Arrays.asList(room.getDeletedBy().split(";")));
                if(!roomDeleters.contains(username)) {
                    roomDeleters.add(username);
                }

                room.setDeletedBy(String.join(";", roomDeleters.stream().filter(s -> !s.isEmpty()).toList()));
                
                List<Message> messages = messageRepository.findAllByRoomId(room.getId());

                messages.forEach(message->{
                    if(message.getSender().equals(username) || message.getRecipient().equals(username)) {
                        List<String> messageDeleters = new ArrayList<>(Arrays.asList(message.getDeletedBy().split(";")));
                        if(!messageDeleters.contains(username)) {
                            messageDeleters.add(username);
                        }
        
                        message.setDeletedBy(String.join(";", messageDeleters.stream().filter(s -> !s.isEmpty()).toList()));
                        
                        messageRepository.save(message);
                    }
                });
                
                if(room.getUser1().equals(username)) {
                    room.setUnreadMessagesNumber1(0);
                }else {
                    room.setUnreadMessagesNumber2(0);
                }

                return Optional.of(roomRepository.save(room)).map(roomMapper::toDto);
            }
        }

        return Optional.empty();
    }

    public Optional<RoomDTO> softDeleteForAllUsers(String id) {
        log.debug("Request to soft delete Room for all users : {}", id);
        Optional<Room> roomOptional = roomRepository.findById(id);

        if(roomOptional.isPresent()) {
            Room room = roomOptional.get();

            List<String> users = new ArrayList<>();
            users.add(room.getUser1());
            users.add(room.getUser2());

            room.setDeletedBy(String.join(";", users.stream().filter(s -> !s.isEmpty()).toList()));

            List<Message> messages = messageRepository.findAllByRoomId(room.getId());

            messages.forEach(message->{
                List<String> messageDeleters = new ArrayList<>(Arrays.asList(message.getDeletedBy().split(";")));
                if(!messageDeleters.contains(room.getUser1())) {
                    messageDeleters.add(room.getUser1());
                }

                if(!messageDeleters.contains(room.getUser2())) {
                    messageDeleters.add(room.getUser2());
                }

                message.setDeletedBy(String.join(";", messageDeleters.stream().filter(s -> !s.isEmpty()).toList()));
                
                messageRepository.save(message);
            });
            
            room.setUnreadMessagesNumber1(0);
            room.setUnreadMessagesNumber2(0);

            return Optional.of(roomRepository.save(room)).map(roomMapper::toDto);
        }

        return Optional.empty();
    }
}
