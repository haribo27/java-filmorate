package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.userRequest.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.userRequest.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.util.RequestStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class UserService {

    private final UserStorage userRepository;

    public UserService(@Qualifier("UserRepo") UserStorage userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(NewUserRequest request) {
        log.trace("Create user: {}", request);
        checkIfNameIsBlank(request);
        User user = UserMapper.mapToUser(request);
        user = userRepository.save(user);
        log.info("Created new user and saved in db: {}", user);
        return UserMapper.mapToUserDto(user);
    }

    public void deleteUser(long id) {
        log.info("Deleting user with id: {}", id);
        getUserOrException(id);
        userRepository.deleteUser(id);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        log.trace("Update user with id: {}", request.getId());
        User updatedUser = userRepository.findUserById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new EntityNotFoundException("Пользватель не найден"));
        updatedUser = userRepository.updateUser(updatedUser);
        log.info("User with id {} updated", request.getId());
        return UserMapper.mapToUserDto(updatedUser);
    }

    public List<UserDto> getAllUsers() {
        log.info("Get all users");
        return userRepository.getAllUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserOrException(long userId) {
        log.info("Getting user with id {}", userId);
        return userRepository.findUserById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким id не найден"));
    }

    public void addFriend(long fromUserId, long toFriendId) {
        log.info("Add request from user {} to user {}", fromUserId, toFriendId);
        User user = userRepository.findUserById(fromUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь  с таким id не найден"));
        User friend = userRepository.findUserById(toFriendId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь  с таким id не найден"));
        if (friend.getFriends().contains(user.getId())) {
            user.getFriends().add(friend.getId());
            userRepository.addFriend(fromUserId, toFriendId, RequestStatus.CONFIRMED);
        } else {
            user.getFriends().add(toFriendId);
            userRepository.addFriend(fromUserId, toFriendId, RequestStatus.NOT_CONFIRMED);
        }
        log.info("Added friend {} to user {}", fromUserId, toFriendId);
    }

    public void deleteFriend(long userId, long friendId) {
        log.info("Delete friend {} from user {}", friendId, userId);
        isUsersExists(userId, friendId);
        int rows = userRepository.deleteFriend(userId, friendId);
        if (rows > 1) {
            log.info("Deleted friend {} from user {}", friendId, userId);
        } else {
            log.info("Friend does not delete from user {}", userId);
        }
    }

    public List<UserDto> getCommonFriends(long userId, long otherId) {
        log.info("Get common friends {} from user {}", otherId, userId);
        Set<Long> commonIds = getUserFriends(userId)
                .stream()
                .filter(id -> getUserFriends(otherId).contains(id))
                .map(UserDto::getId)
                .collect(Collectors.toSet());
        return commonIds.stream().map(this::getUserOrException).toList();
    }

    public List<UserDto> getUserFriends(long id) {
        log.info("Getting friends from user id {}", id);
        getUserOrException(id);
        return userRepository.getUserFriends(id)
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    private void checkIfNameIsBlank(NewUserRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            log.info("Name null ? -> Name = login");
            request.setName(request.getLogin());
        }
        log.trace("Name is not null");
    }

    public void isUsersExists(long userId, long friendId) {
        userRepository.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь  с таким id не найден"));
        userRepository.findUserById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь  с таким id не найден"));
    }
}
