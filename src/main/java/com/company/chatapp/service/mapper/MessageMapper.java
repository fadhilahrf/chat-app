package com.company.chatapp.service.mapper;

import com.company.chatapp.domain.Message;
import com.company.chatapp.domain.Room;
import com.company.chatapp.service.dto.MessageDTO;
import com.company.chatapp.service.dto.RoomDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Message} and its DTO {@link MessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {
    @Mapping(target = "replyTo", source = "replyTo", qualifiedByName = "replyTo")
    MessageDTO toDto(Message s);

    @Named("replyTo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sender", source = "sender")
    @Mapping(target = "content", source = "content")
    MessageDTO toDtoReplyTo(Message message);
}
