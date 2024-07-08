package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public void createUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    public Map<Long, User> getUsers() {
        return Map.copyOf(users);
    }
}
