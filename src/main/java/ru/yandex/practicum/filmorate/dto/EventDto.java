package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventTypeFeed;
import ru.yandex.practicum.filmorate.model.OperationFeed;

@Builder
@Data
public class EventDto {
    private Long eventId;
    private Long timestamp;
    private Long userId;
    private EventTypeFeed eventType;
    private OperationFeed operation;
    private Long entityId;
}