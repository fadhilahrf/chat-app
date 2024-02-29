package com.company.chatapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.chatapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoomDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoomDTO.class);
        RoomDTO roomDTO1 = new RoomDTO();
        roomDTO1.setId("id1");
        RoomDTO roomDTO2 = new RoomDTO();
        assertThat(roomDTO1).isNotEqualTo(roomDTO2);
        roomDTO2.setId(roomDTO1.getId());
        assertThat(roomDTO1).isEqualTo(roomDTO2);
        roomDTO2.setId("id2");
        assertThat(roomDTO1).isNotEqualTo(roomDTO2);
        roomDTO1.setId(null);
        assertThat(roomDTO1).isNotEqualTo(roomDTO2);
    }
}
