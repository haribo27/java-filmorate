package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"eventId"})
@Builder
public class EventFeed {
    private Long eventId;
    private Long timestamp;
    private Long userId;
    private EventTypeFeed eventType;
    private OperationFeed operation;
    private Long entityId;
}