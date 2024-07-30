package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.RequestStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public class FriendRepository extends BaseRepository<User> {

    private static final String INSERT_QUERY = "INSERT INTO friends (user_id, friend_id, request_status)" +
            "VALUES (?, ?, ?)";

    private static final String FIND_FRIENDS = "SELECT u.* FROM users AS u JOIN friends AS f ON u.id = f.friend_id WHERE f.user_id = ?";
    private static final String FIND_FRIENDS_REQUEST = "SELECT request_status FROM friends WHERE user_id = ? AND friend_id = ?";
    private final UserRepository userRepository;

    private final JdbcTemplate jdbcTemplate;

    public FriendRepository(JdbcTemplate jdbc, RowMapper<User> mapper, UserRepository userRepository, JdbcTemplate jdbcTemplate) {
        super(jdbc, mapper);
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public long addFriend(long userId, long friendId, RequestStatus requestStatus) {
       return jdbcTemplate.update(INSERT_QUERY,userId,friendId,requestStatus.toString());
    }

    public List<User> getUserFriends(long userId) {
        return jdbcTemplate.query(FIND_FRIENDS,mapper,userId);
    }
}
