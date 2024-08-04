package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    void deleteUser(long id);

    User updateUser(User user);

    Optional<User> findUserById(long id);

    List<User> getAllUsers();

    void addFriend(long userId, long friendId, RequestStatus requestStatus);

    int deleteFriend(long userId, long friendId);

    List<User> getUserFriends(long userId);
}
