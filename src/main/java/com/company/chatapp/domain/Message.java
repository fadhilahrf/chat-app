package com.company.chatapp.domain;

import jakarta.validation.constraints.*;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Message.
 */
@Document(collection = "message")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Message extends AbstractAuditingEntity<String> {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("sender")
    private String sender;

    @NotNull
    @Field("recipient")
    private String recipient;

    @NotNull
    @Field("content")
    private String content;

    @Field("roomId")
    private String roomId;

    @Field("delivery_time")
    private Instant deliveryTime = Instant.now();

    @Field("read")
    private Boolean read = false;

    @Field("is_edited")
    private Boolean isEdited = false;

    @Field("deleted_by")
    private String deletedBy="";

    @DBRef
    @Field("replay_to")
    @JsonIgnoreProperties(value = { "replyTo" }, allowSetters = true)
    private Message replyTo;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return this.sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Instant getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Instant deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Message getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Message replyTo) {
        this.replyTo = replyTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return getId() != null && getId().equals(((Message) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", sender='" + getSender() + "'" +
            ", recipient='" + getRecipient() + "'" +
            ", content='" + getContent() + "'" +
            "}";
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
