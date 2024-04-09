package com.company.chatapp.domain;

import jakarta.validation.constraints.*;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Room.
 */
@Document(collection = "room")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Room extends AbstractAuditingEntity<String> {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("user_1")
    private String user1;

    @NotNull
    @Field("user_2")
    private String user2;

    @Field("deleted_by")
    private String deletedBy="";

    @Field("latest_message_time")
    private Instant latestMessageTime;

    @Field("unread_messages_number1")
    private Integer unreadMessagesNumber1 = 0;
    
    @Field("unread_messages_number2")
    private Integer unreadMessagesNumber2 = 0;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser1() {
        return this.user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return this.user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getDeletedBy() {
        return this.deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room)) {
            return false;
        }
        return getId() != null && getId().equals(((Room) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Room{" +
            "id=" + getId() +
            ", user1='" + getUser1() + "'" +
            ", user2='" + getUser2() + "'" +
            ", deleted1='" + getDeletedBy() + "'" +
            "}";
    }
}
