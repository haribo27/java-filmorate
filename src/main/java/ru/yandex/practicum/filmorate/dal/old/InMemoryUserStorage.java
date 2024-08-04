package ru.yandex.practicum.filmorate.dal.old;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.UserStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.RequestStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long currentMaxId = 1;

    @Override
    public User save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public void addFriend(long userId, long friendId, RequestStatus requestStatus) {

    }

    @Override
    public int deleteFriend(long userId, long friendId) {
        return 0;
    }

    @Override
    public List<User> getUserFriends(long userId) {
        return List.of();
    }

    private long getNextId() {
        return currentMaxId++;
    }
}
