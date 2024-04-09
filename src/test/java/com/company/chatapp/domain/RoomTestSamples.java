package com.company.chatapp.domain;

import java.util.UUID;

public class RoomTestSamples {

    public static Room getRoomSample1() {
        Room room = new Room();
        room.setId("id1");
        room.setUser1("user11");
        room.setUser2("user21");
        return room;
    }

    public static Room getRoomSample2() {
        Room room = new Room();
        room.setId("id2");
        room.setUser1("user12");
        room.setUser2("user22");
        return room;
    }

    public static Room getRoomRandomSampleGenerator() {
        Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        room.setUser1(UUID.randomUUID().toString());
        room.setUser2(UUID.randomUUID().toString());
        return room;
    }
}
