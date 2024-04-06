package com.company.chatapp.service;

import com.company.chatapp.domain.Message;
import com.company.chatapp.domain.Room;
import com.company.chatapp.repository.MessageRepository;
import com.company.chatapp.security.SecurityUtils;
import com.company.chatapp.service.dto.MessageDTO;
import com.company.chatapp.service.dto.RoomDTO;
import com.company.chatapp.service.mapper.MessageMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.company.chatapp.domain.Message}.
 */
@Service
public class MessageService {

    private final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;

    private final RoomService roomService;

    private final MessageMapper messageMapper;

    public MessageService(MessageRepository messageRepository, MessageMapper messageMapper, RoomService roomService) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.roomService = roomService;
    }

    /**
     * Save a message.
     *
     * @param messageDTO the entity to save.
     * @return the persisted entity.
     */
    public MessageDTO save(MessageDTO messageDTO)  {
        log.debug("Request to save Message : {}", messageDTO);
        Message message = messageMapper.toEntity(messageDTO);
        Room room = roomService.findOrCreate(messageDTO.getSender(), messageDTO.getRecipient());
        room.setLatestMessageTime(Instant.now());
        if(messageDTO.getRead()){
            if(messageDTO.getRecipient().equals(room.getUser1()) && room.getUnreadMessagesNumber1()>0) {
                room.setUnreadMessagesNumber1(room.getUnreadMessagesNumber1()-1);
            }else if(messageDTO.getRecipient().equals(room.getUser2()) && room.getUnreadMessagesNumber2()>0) {
                room.setUnreadMessagesNumber2(room.getUnreadMessagesNumber2()-1);
            }
        }else {
            if(messageDTO.getRecipient().equals(room.getUser1())) {
                room.setUnreadMessagesNumber1(room.getUnreadMessagesNumber1()+1);
            }else{
                room.setUnreadMessagesNumber2(room.getUnreadMessagesNumber2()+1);
            }
        }
        List<String> roomDeleters = new ArrayList<>(Arrays.asList(room.getDeletedBy().split(";")));

        if(roomDeleters.contains(message.getSender())) {
            final String sender = message.getSender();
            roomDeleters.removeIf(deleter->deleter.equals(sender));
        }

        if(roomDeleters.contains(message.getRecipient())) {
            final String recipient = message.getRecipient();
            roomDeleters.removeIf(deleter->deleter.equals(recipient));
        }

        System.out.println("ROOM DELETE : "+roomDeleters);

        room.setDeletedBy(String.join(";", roomDeleters.stream().filter(s -> !s.isEmpty()).toList()));

        roomService.save(new RoomDTO(room));
        message.setRoom(room);
        
        message = messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    /**
     * Update a message.
     *
     * @param messageDTO the entity to save.
     * @return the persisted entity.
     */
    public MessageDTO update(MessageDTO messageDTO) {
        log.debug("Request to update Message : {}", messageDTO);
        Message message = messageMapper.toEntity(messageDTO);
        message = messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    public Optional<MessageDTO> editMessage(MessageDTO messageDTO) {
        log.debug("Request to edit Message : {}", messageDTO);
        Optional<Message> messageOptional = messageRepository.findById(messageDTO.getId());

        if(messageOptional.isPresent()) {
            if(!messageDTO.getContent().equals(messageOptional.get().getContent())) {
                Message message = messageOptional.get();
                message.setIsEdited(true);
                message.setContent(messageDTO.getContent());
                message = messageRepository.save(message);
                return Optional.of(messageMapper.toDto(message));
            }
        }
        return Optional.empty();
    }

    /**
     * Partially update a message.
     *
     * @param messageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MessageDTO> partialUpdate(MessageDTO messageDTO) {
        log.debug("Request to partially update Message : {}", messageDTO);

        return messageRepository
            .findById(messageDTO.getId())
            .map(existingMessage -> {
                messageMapper.partialUpdate(existingMessage, messageDTO);

                return existingMessage;
            })
            .map(messageRepository::save)
            .map(messageMapper::toDto);
    }

    /**
     * Get all the messages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<MessageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Messages");
        return messageRepository.findAll(pageable).map(messageMapper::toDto);
    }

    public List<MessageDTO> findAllBySenderAndRecipient(String sender, String recipient) {
        log.debug("Request to find all by Sender and Recipient: {} {}", sender, recipient);
        Optional<Room> roomOptional = roomService.findByUser1AndUser2(sender, recipient);
        if(roomOptional.isPresent()){
            Room room = roomOptional.get();
            return messageRepository.findAllByRoom(room).stream().filter(message->{
                List<String> messageDeleters = new ArrayList<>(Arrays.asList(message.getDeletedBy().split(";")));
                List<String> roomDeleters = new ArrayList<>(Arrays.asList(room.getDeletedBy().split(";")));
                return !messageDeleters.contains(sender) && !roomDeleters.contains(sender);
            }).map(messageMapper::toDto).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<MessageDTO> findAllBySenderAndRecipient(String recipient) {
        log.debug("Request to find all by Sender and Recipient: {}", recipient);
        
        Optional<String> senderOptional = SecurityUtils.getCurrentUserLogin();

        if(senderOptional.isPresent()) {
            return findAllBySenderAndRecipient(senderOptional.get(), recipient);
        }

        return new ArrayList<>();
    }

    /**
     * Get one message by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<MessageDTO> findOne(String id) {
        log.debug("Request to get Message : {}", id);
        return messageRepository.findById(id).map(messageMapper::toDto);
    }

    /**
     * Delete the message by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Message : {}", id);
        messageRepository.deleteById(id);
    }

    public Optional<MessageDTO> softDeleteForUser(String id, String username) {
        log.debug("Request to soft delete Message : {} for user : {}", id, username);
        Optional<Message> messageOptional = messageRepository.findById(id);
        if(messageOptional.isPresent()) {
            Message message = messageOptional.get();
            
            if(message.getSender().equals(username) || message.getRecipient().equals(username)) {
                List<String> messageDeleters = new ArrayList<>(Arrays.asList(message.getDeletedBy().split(";")));
                if(!messageDeleters.contains(username)) {
                    messageDeleters.add(username);
                }

                message.setDeletedBy(String.join(";", messageDeleters.stream().filter(s -> !s.isEmpty()).toList()));
                
                return Optional.of(messageRepository.save(message)).map(messageMapper::toDto);
            }
        }
        return Optional.empty();
    }

    public Optional<MessageDTO> softDeleteForAllUsers(String id) {
        log.debug("Request to soft delete Message for all users : {}", id);
        Optional<Message> messageOptional = messageRepository.findById(id);
        if(messageOptional.isPresent()) {
            Message message = messageOptional.get();

            List<String> messageDeleters = new ArrayList<>();
            if(!messageDeleters.contains(message.getSender())) {
                messageDeleters.add(message.getSender());
            }
            if(!messageDeleters.contains(message.getRecipient())) {
                messageDeleters.add(message.getRecipient());
            }

            message.setDeletedBy(String.join(";", messageDeleters.stream().filter(s -> !s.isEmpty()).toList()));
            
            return Optional.of(messageRepository.save(message)).map(messageMapper::toDto);
        }
        return Optional.empty();
    }
}
