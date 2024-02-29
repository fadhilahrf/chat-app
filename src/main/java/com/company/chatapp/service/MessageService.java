package com.company.chatapp.service;

import com.company.chatapp.domain.Message;
import com.company.chatapp.domain.Room;
import com.company.chatapp.repository.MessageRepository;
import com.company.chatapp.security.SecurityUtils;
import com.company.chatapp.service.dto.MessageDTO;
import com.company.chatapp.service.mapper.MessageMapper;

import javassist.NotFoundException;

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
        Room room = this.roomService.findOrCreate(messageDTO.getSender(), messageDTO.getRecipient());
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

    public List<MessageDTO> findAllBySenderAndRecipient(String user1, String user2) {
        log.debug("findAllBySenderAndRecipient: {} {}", user1, user2);
        Room room = this.roomService.findOrCreate(user1, user2);
        return messageRepository.findAllByRoom(room).stream().map(messageMapper::toDto).toList();
    }

    public List<MessageDTO> findAllBySenderAndRecipient(String recipient) {
        log.debug("findAllBySenderAndRecipient: {}", recipient);
        
        String sender="";

        try {
            sender = SecurityUtils.getCurrentUserLogin().orElseThrow(()-> new NotFoundException("Username not found"));

        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return findAllBySenderAndRecipient(sender, recipient);
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
}
