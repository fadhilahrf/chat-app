package com.company.chatapp.web.rest;

import com.company.chatapp.repository.RoomRepository;
import com.company.chatapp.service.RoomService;
import com.company.chatapp.service.dto.RoomDTO;
import com.company.chatapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.company.chatapp.domain.Room}.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomResource {

    private final Logger log = LoggerFactory.getLogger(RoomResource.class);

    private static final String ENTITY_NAME = "room";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RoomService roomService;

    private final RoomRepository roomRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public RoomResource(RoomService roomService, RoomRepository roomRepository, SimpMessagingTemplate messagingTemplate) {
        this.roomService = roomService;
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * {@code POST  /rooms} : Create a new room.
     *
     * @param roomDTO the roomDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new roomDTO, or with status {@code 400 (Bad Request)} if the room has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomDTO roomDTO) throws URISyntaxException {
        log.debug("REST request to save Room : {}", roomDTO);
        if (roomDTO.getId() != null) {
            throw new BadRequestAlertException("A new room cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RoomDTO result = roomService.save(roomDTO);
        return ResponseEntity
            .created(new URI("/api/rooms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /rooms/:id} : Updates an existing room.
     *
     * @param id the id of the roomDTO to save.
     * @param roomDTO the roomDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roomDTO,
     * or with status {@code 400 (Bad Request)} if the roomDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roomDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody RoomDTO roomDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Room : {}, {}", id, roomDTO);
        if (roomDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roomDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RoomDTO result = roomService.update(roomDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, roomDTO.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /rooms/:id} : Partial updates given fields of an existing room, field will ignore if it is null
     *
     * @param id the id of the roomDTO to save.
     * @param roomDTO the roomDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roomDTO,
     * or with status {@code 400 (Bad Request)} if the roomDTO is not valid,
     * or with status {@code 404 (Not Found)} if the roomDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the roomDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RoomDTO> partialUpdateRoom(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody RoomDTO roomDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Room partially : {}, {}", id, roomDTO);
        if (roomDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roomDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RoomDTO> result = roomService.partialUpdate(roomDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, roomDTO.getId())
        );
    }

    /**
     * {@code GET  /rooms} : get all the rooms.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rooms in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RoomDTO>> getAllRooms(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Rooms");
        Page<RoomDTO> page = roomService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/user")
    public ResponseEntity<List<RoomDTO>> getAllRoomsForUser() {
        log.debug("REST request to get a page of Rooms");
        List<RoomDTO> rooms = roomService.findAllByCurrentUser();
        return ResponseEntity.ok().body(rooms);
    }

    @GetMapping("/user/latest-message")
    public ResponseEntity<List<RoomDTO>> getAllRoomsForUserSortedByLatestMessageTime() {
        log.debug("REST request to get a page of Rooms");
        List<RoomDTO> rooms = roomService.findAllByCurrentUserSortedByLatestMessageTime();
        return ResponseEntity.ok().body(rooms);
    }

    /**
     * {@code GET  /rooms/:id} : get the "id" room.
     *
     * @param id the id of the roomDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roomDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable("id") String id) {
        log.debug("REST request to get Room : {}", id);
        Optional<RoomDTO> roomDTO = roomService.findOne(id);
        return ResponseUtil.wrapOrNotFound(roomDTO);
    }

    /**
     * {@code DELETE  /rooms/:id} : delete the "id" room.
     *
     * @param id the id of the roomDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") String id) {
        log.debug("REST request to delete Room : {}", id);
        roomService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }

    @MessageMapping("/room/delete/{id}/user/{username}")
    public void softDeleteForUser(@DestinationVariable("id") String id, @DestinationVariable("username") String username) {
        log.debug("REST request to soft delete Room : {} by {}", id, username);
        Optional<RoomDTO> roomOptional = roomService.softDeleteForUser(id, username);
        if(roomOptional.isPresent()) {
            RoomDTO roomDTO = roomOptional.get();
            roomDTO.setLatestMessage(null);
            roomDTO.setLatestMessageTime(null);

            messagingTemplate.convertAndSendToUser(
                username, "/room/deleted",
                roomDTO
            );
        }
    }

    @MessageMapping("/room/delete/{id}/all-users")
    public void softDeleteByAllUsers(@DestinationVariable("id") String id) {
        log.debug("REST request to soft delete Message for All Users : {}", id);
        Optional<RoomDTO> roomOptional = roomService.softDeleteForAllUsers(id);
        if(roomOptional.isPresent()) {
            RoomDTO roomDTO = roomOptional.get();
            roomDTO.setLatestMessage(null);
            roomDTO.setLatestMessageTime(null);

            messagingTemplate.convertAndSendToUser(
                roomDTO.getUser1(), "/room/deleted",
                roomDTO
            );

            messagingTemplate.convertAndSendToUser(
                roomDTO.getUser2(), "/room/deleted",
                roomDTO
            );
        }
    }
}
