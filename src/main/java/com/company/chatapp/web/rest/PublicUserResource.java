package com.company.chatapp.web.rest;

import com.company.chatapp.service.UserService;
import com.company.chatapp.service.dto.UserDTO;

import io.micrometer.common.util.StringUtils;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api")
public class PublicUserResource {

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(
        Arrays.asList("id", "login", "firstName", "lastName", "email", "activated", "langKey")
    );

    private final Logger log = LoggerFactory.getLogger(PublicUserResource.class);

    private final UserService userService;

    public PublicUserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@code GET /users} : get all users with only public information - calling this method is allowed for anyone.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */

    @GetMapping("/users/{login}")
    public ResponseEntity<UserDTO> getPublicUser(@PathVariable("login") String login) {
        log.debug("REST request to get public User");
        Optional<UserDTO> userOptional = userService.findOneByLogin(login);
        return ResponseUtil.wrapOrNotFound(userOptional);
    }
 

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllPublicUsers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get all public User names");
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<UserDTO> page = userService.getAllPublicUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<UserDTO>> getAllPublicUsers(@RequestParam(value = "search", required = false) String search) {
        log.debug("REST request to get all public User names");

        List<UserDTO> users;

        if(StringUtils.isBlank(search)) {
            users = userService.getAllPublicUsers();
        }else {
            users = userService.getAllByLoginLike(search);
        }
        
        return ResponseEntity.ok(users);
    }

    /**
     * Gets a list of all roles.
     * @return a string list of all roles.
     */
    @GetMapping("/authorities")
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }

    @MessageMapping("/user.connect")
    @SendTo("/topic/public/connection")
    public UserDTO connectUser(UserDTO userDTO) {
        return new UserDTO(userService.setCurrentUserOnlineStatus(userDTO.getLogin(), true));
    }

    @MessageMapping("/user.disconnect")
    @SendTo("/topic/public/connection")
    public UserDTO disconnectUser(UserDTO userDTO) {
        return new UserDTO(userService.setCurrentUserOnlineStatus(userDTO.getLogin(), false));
    }
}
