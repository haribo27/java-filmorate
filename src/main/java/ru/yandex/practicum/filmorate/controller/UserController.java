package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Get all users");
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.trace("Create user: {}", user);
        if (user.getName() == null) {
            log.info("Name null ? -> Name = login");
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Created new user: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.trace("Update user: {}", user);
        if (user.getId() == null) {
            log.warn("Id can not be null");
            throw new ValidationException("User id is null");
        }
        if (users.get(user.getId()) == null) {
            log.warn("User not found");
            throw new ValidationException("User not found");
        }
        users.put(user.getId(), user);
        log.info("Found updated user: {}", user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("Current maxId: {}", currentMaxId);
        return ++currentMaxId;
    }
}
