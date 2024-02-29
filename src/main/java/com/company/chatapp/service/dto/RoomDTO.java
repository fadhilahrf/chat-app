package com.company.chatapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

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

    private Boolean deleted1;

    private Boolean deleted2;

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

    public Boolean getDeleted1() {
        return deleted1;
    }

    public void setDeleted1(Boolean deleted1) {
        this.deleted1 = deleted1;
    }

    public Boolean getDeleted2() {
        return deleted2;
    }

    public void setDeleted2(Boolean deleted2) {
        this.deleted2 = deleted2;
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
            ", deleted1='" + getDeleted1() + "'" +
            ", deleted2='" + getDeleted2() + "'" +
            "}";
    }
}
