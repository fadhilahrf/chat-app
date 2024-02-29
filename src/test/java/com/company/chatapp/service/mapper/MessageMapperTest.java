package com.company.chatapp.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class MessageMapperTest {

    private MessageMapper messageMapper;

    @BeforeEach
    public void setUp() {
        messageMapper = new MessageMapperImpl();
    }
}
