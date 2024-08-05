package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("UserRepo")
public class UserRepository extends BaseRepository<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, name, login , birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, name = ?," +
            " login = ?, birthday = ? WHERE id = ?";

    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";

    private static final String INSERT_QUERY_FRIEND = "INSERT INTO friends (user_id, friend_id)" +
            "VALUES (?, ?)";

    private static final String FIND_FRIENDS = "SELECT u.* FROM users AS u " +
            "JOIN friends AS f ON u.id = f.friend_id WHERE f.user_id = ?";

    private static final String DELETE_QUERY_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper, JdbcTemplate jdbcTemplate) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getName(),
                user.getLogin(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        delete(
                DELETE_QUERY,
                id
        );
    }

    @Override
    public User updateUser(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public Optional<User> findUserById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public List<User> getAllUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    public void addFriend(long fromUser, long toFriend) {
        jdbcTemplate.update(INSERT_QUERY_FRIEND, fromUser, toFriend);
    }

    public List<User> getUserFriends(long userId) {
        return jdbcTemplate.query(FIND_FRIENDS, mapper, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        Set<Long> commonIds = getUserFriends(userId)
                .stream()
                .filter(id -> getUserFriends(otherId).contains(id))
                .map(User::getId)
                .collect(Collectors.toSet());
        return commonIds.stream()
                .map(this::findUserById)
                .map(user -> user.orElse(null)).toList();
    }

    public int deleteFriend(long userId, long friendId) {
        return jdbcTemplate.update(DELETE_QUERY_FRIEND, userId, friendId);
    }
}
