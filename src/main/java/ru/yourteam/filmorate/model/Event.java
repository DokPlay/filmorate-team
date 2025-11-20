package ru.yourteam.filmorate.model;

import lombok.Data;

@Data
public class Event {

    private int eventId;
    private long timestamp;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private int entityId;
}
