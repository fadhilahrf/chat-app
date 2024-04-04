package com.company.chatapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.company.chatapp.domain.Room;

/**
 * A DTO for the {@link com.company.chatapp.domain.Room} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoomDTO implements Serializable {

    private String id;

    @NotNull
    private String user1;

    @NotNull
    private String user2;

    private String deletedBy="";

    private Instant latestMessageTime;

    private Integer unreadMessagesNumber1 = 0;
    
    private Integer unreadMessagesNumber2 = 0;

    private MessageDTO latestMessage;

    public RoomDTO() {}

    public RoomDTO(Room room) {
        this.id = room.getId();
        this.user1 = room.getUser1();
        this.user2 = room.getUser2();
        this.deletedBy = room.getDeletedBy();
        this.latestMessageTime = room.getLatestMessageTime();
        this.unreadMessagesNumber1 = room.getUnreadMessagesNumber1();
        this.unreadMessagesNumber2 = room.getUnreadMessagesNumber2();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getDeletedBy() {
        return this.deletedBy;
    }

    public Instant getLatestMessageTime() {
        return latestMessageTime;
    }

    public void setLatestMessageTime(Instant latestMessageTime) {
        this.latestMessageTime = latestMessageTime;
    }

    public Integer getUnreadMessagesNumber1() {
        return unreadMessagesNumber1;
    }

    public void setUnreadMessagesNumber1(Integer unreadMessagesNumber1) {
        this.unreadMessagesNumber1 = unreadMessagesNumber1;
    }

    public Integer getUnreadMessagesNumber2() {
        return unreadMessagesNumber2;
    }

    public void setUnreadMessagesNumber2(Integer unreadMessagesNumber2) {
        this.unreadMessagesNumber2 = unreadMessagesNumber2;
    }

    public MessageDTO getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(MessageDTO latestMessage) {
        this.latestMessage = latestMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoomDTO)) {
            return false;
        }

        RoomDTO roomDTO = (RoomDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, roomDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoomDTO{" +
            "id='" + getId() + "'" +
            ", user1='" + getUser1() + "'" +
            ", user2='" + getUser2() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
