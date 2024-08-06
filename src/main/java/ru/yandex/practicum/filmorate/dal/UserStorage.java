package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    void deleteUser(long id);

    User updateUser(User user);

    Optional<User> findUserById(long id);

    List<User> getAllUsers();

    void addFriend(long userId, long friendId);

    int deleteFriend(long userId, long friendId);

    List<User> getUserFriends(long userId);

    List<User> getCommonFriends(long userId, long otherId);
}
