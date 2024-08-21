package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.EventFeed;
import ru.yandex.practicum.filmorate.model.EventTypeFeed;
import ru.yandex.practicum.filmorate.model.OperationFeed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventFeedMapper implements RowMapper<EventFeed> {
    @Override
    public EventFeed mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EventFeed.builder()
                .eventId(rs.getLong("event_id"))
                .timestamp(rs.getTimestamp("timestamp").getTime())
                .userId(rs.getLong("user_id"))
                .eventType(EventTypeFeed.valueOf(rs.getString("event_type")))
                .operation(OperationFeed.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .build();
    }

    public static EventDto mapToEventDto(EventFeed event) {
        return EventDto.builder()
                .eventId(event.getEventId())
                .timestamp(event.getTimestamp())
                .userId(event.getUserId())
                .eventType(event.getEventType())
                .operation(event.getOperation())
                .operation(event.getOperation())
                .entityId(event.getEntityId())
                .build();
    }
}
