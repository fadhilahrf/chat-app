package com.company.chatapp.domain;

import static com.company.chatapp.domain.MessageTestSamples.*;
import static com.company.chatapp.domain.RoomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.company.chatapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Message.class);
        Message message1 = getMessageSample1();
        Message message2 = new Message();
        assertThat(message1).isNotEqualTo(message2);

        message2.setId(message1.getId());
        assertThat(message1).isEqualTo(message2);

        message2 = getMessageSample2();
        assertThat(message1).isNotEqualTo(message2);
    }

    @Test
    void roomTest() throws Exception {
        Message message = getMessageRandomSampleGenerator();
        Room roomBack = getRoomRandomSampleGenerator();

        message.setRoom(roomBack);
        assertThat(message.getRoom()).isEqualTo(roomBack);

        message.room(null);
        assertThat(message.getRoom()).isNull();
    }
}
