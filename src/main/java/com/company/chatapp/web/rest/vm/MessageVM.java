package com.company.chatapp.web.rest.vm;

import java.time.Instant;

import com.company.chatapp.service.dto.MessageDTO;

public class MessageVM {

    private MessageDTO messageDTO;

    private Instant deliveryTime = Instant.now();

    public MessageDTO getMessageDTO() {
        return messageDTO;
    }

    public void setMessageDTO(MessageDTO messageDTO) {
        this.messageDTO = messageDTO;
    }

    public Instant getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Instant deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
