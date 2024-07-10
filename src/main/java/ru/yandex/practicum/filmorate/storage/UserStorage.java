package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    void createUser(User user);

    void deleteUser(long id);

    void updateUser(User user);

    Optional<User> getUser(long id);

    Collection<User> getAllUsers();
}
