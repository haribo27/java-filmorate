package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void createUser(User user) {
        log.trace("Create user: {}", user);
        if (user.getName() == null) {
            log.info("Name null ? -> Name = login");
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        user.setFriends(new HashSet<>());
        inMemoryUserStorage.createUser(user);
        log.info("Created new user: {}", user);
    }

    public void deleteUser(long id) {
        checkUserExist(id);
        inMemoryUserStorage.deleteUser(id);
    }

    public void updateUser(User user) {
        log.trace("Update user: {}", user);
        if (user.getId() == null) {
            log.warn("Id can not be null");
            throw new ValidationException("User id is null");
        }
        checkUserExist(user.getId());
        inMemoryUserStorage.updateUser(user);
        log.info("Found updated user: {}", user);
    }

    public Collection<User> getAllUsers() {
        log.info("Get all users");
        return inMemoryUserStorage.getAllUsers();
    }

    public void addFriend(long userId, long friendId) {
        checkUserExist(userId);
        checkUserExist(friendId);
        log.info("Add friend {} to user {}", friendId, userId);
        getUser(userId).getFriends().add(friendId);
        getUser(friendId).getFriends().add(userId);
        log.info("Added friend {} to user {}", friendId, userId);
    }

    public void deleteFriend(long userId, long friendId) {
        checkUserExist(userId);
        checkUserExist(friendId);
        log.info("Delete friend {} from user {}", friendId, userId);
        getUser(userId).getFriends().remove(friendId);
        getUser(friendId).getFriends().remove(userId);
        log.info("Deleted friend {} from user {}", friendId, userId);
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        checkUserExist(userId);
        checkUserExist(otherId);
        log.info("Get common friends {} from user {}", otherId, userId);
        Set<Long> commonIds = getUser(otherId)
                .getFriends()
                .stream()
                .filter(id -> getUser(userId).getFriends().contains(id))
                .collect(Collectors.toSet());
        return commonIds.stream().map(id -> inMemoryUserStorage.getUsers().get(id)).toList();
    }

    public Collection<User> getUserFriends(long id) {
        checkUserExist(id);
        return getUser(id).getFriends()
                .stream()
                .map(userId -> inMemoryUserStorage.getUsers().get(userId))
                .toList();
    }

    private long getNextId() {
        long currentMaxId = inMemoryUserStorage.getUsers().keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("Current maxId: {}", currentMaxId);
        return ++currentMaxId;
    }

    protected void checkUserExist(long id) {
        if (inMemoryUserStorage.getUsers().get(id) == null) {
            throw new UserNotFoundException("User with id: " + id + " nod found");
        }
    }

    public User getUser(long id) {

        return inMemoryUserStorage.getUsers().get(id);
    }
}
