package ru.yourteam.filmorate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "events")
public class Event {

    // Уникальный идентификатор события ленты
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;

    // Метка времени в миллисекундах (BIGINT)
    @Column(name = "event_timestamp", nullable = false)
    private Long timestamp;

    // Автор события
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    // Тип события, хранится строкой согласно CHECK-ограничению
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private EventType eventType;

    // Операция над сущностью, тоже строка в БД
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Operation operation;

    // Идентификатор связанной сущности
    @Column(name = "entity_id", nullable = false)
    private Integer entityId;
}
