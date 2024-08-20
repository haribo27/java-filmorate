package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.EventFeedMapper;
import ru.yandex.practicum.filmorate.model.EventFeed;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository("EventFeedRepo")
public class EventFeedRepository extends BaseRepository<EventFeed> implements EventFeedStorage {

    EventFeedMapper extractor;

    public EventFeedRepository(JdbcTemplate jdbc, RowMapper<EventFeed> mapper, EventFeedMapper extractor) {
        super(jdbc, mapper);
        this.extractor = extractor;
    }

    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM events_feed WHERE user_id = ?";

    @Override
    public List<EventFeed> getByUserId(int userId) {
        return findMany(FIND_BY_USER_ID_QUERY, userId);
    }

    @Override
    public void save(EventFeed event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(this.jdbc)
                .withTableName("events_feed")
                .usingGeneratedKeyColumns("event_id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("timestamp", Timestamp.valueOf(LocalDateTime.now()))
                .addValue("user_id", event.getUserId())
                .addValue("event_type", event.getEventType())
                .addValue("operation", event.getOperation())
                .addValue("entity_id", event.getEntityId());

        simpleJdbcInsert.execute(parameters);
    }


}
