package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage inMemoryUserStorage;

    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void createUser(User user) {
        log.trace("Create user: {}", user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Name null ? -> Name = login");
            user.setName(user.getLogin());
        }
        user.setFriends(new HashSet<>());
        inMemoryUserStorage.createUser(user);
        log.info("Created new user: {}", user);
    }

    public void deleteUser(long id) {
        getUserOrException(id);
        inMemoryUserStorage.deleteUser(id);
    }

    public void updateUser(User user) {
        log.trace("Update user: {}", user);
        if (user.getId() == null) {
            log.warn("Id can not be null");
            throw new ValidationException("User id is null");
        }
        getUserOrException(user.getId());
        inMemoryUserStorage.updateUser(user);
        log.info("Found updated user: {}", user);
    }

    public Collection<User> getAllUsers() {
        log.info("Get all users");
        return inMemoryUserStorage.getAllUsers();
    }

    public void addFriend(long userId, long friendId) {
        log.info("Add friend {} to user {}", friendId, userId);
        getUserOrException(userId).getFriends().add(friendId);
        getUserOrException(friendId).getFriends().add(userId);
        log.info("Added friend {} to user {}", friendId, userId);
    }

    public void deleteFriend(long userId, long friendId) {
        log.info("Delete friend {} from user {}", friendId, userId);
        getUserOrException(userId).getFriends().remove(friendId);
        getUserOrException(friendId).getFriends().remove(userId);
        log.info("Deleted friend {} from user {}", friendId, userId);
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        log.info("Get common friends {} from user {}", otherId, userId);
        Set<Long> commonIds = getUserOrException(otherId)
                .getFriends()
                .stream()
                .filter(id -> getUserOrException(userId).getFriends().contains(id))
                .collect(Collectors.toSet());
        return commonIds.stream().map(this::getUserOrException).toList();
    }

    public Collection<User> getUserFriends(long id) {
        return getUserOrException(id).getFriends()
                .stream()
                .map(this::getUserOrException)
                .toList();
    }

    public User getUserOrException(long id) {
        return inMemoryUserStorage.getUser(id).orElseThrow(() ->
                new EntityNotFoundException("Фильм с таким id не найден"));
    }
}
