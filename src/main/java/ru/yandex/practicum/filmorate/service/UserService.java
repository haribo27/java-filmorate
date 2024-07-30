package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mappers.FriendRepository;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.RequestStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class UserService {

    private final UserStorage userRepository;
    private final FriendRepository friendRepository;

    public UserService(@Qualifier("UserRepo") UserStorage userRepository,
                       FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    public UserDto createUser(NewUserRequest request) {
        log.trace("Create user: {}", request);
        checkIfNameIsBlank(request);
        request.setFriends(new HashSet<>());

        User user = UserMapper.mapToUser(request);
        user = userRepository.save(user);
        log.info("Created new user and saved in db: {}", user);
        return UserMapper.mapToUserDto(user);
    }

    public boolean deleteUser(long id) {
        log.info("Deleting user with id: {}",id);
        getUserOrException(id);
        return userRepository.deleteUser(id);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        log.trace("Update user with id: {}", request.getId());
        User updatedUser = userRepository.findUserById(request.getId())
                .map(user -> UserMapper.updateUserFields(user,request))
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
        return userRepository.findUserById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким id не найден"));
    }

    public void addFriend(long fromUserId, long toFriendId) {
        log.info("Add request from user {} to user {}", fromUserId, toFriendId);

        User friend = userRepository.findUserById(toFriendId)
                .orElseThrow(()-> new EntityNotFoundException("Пользователь  с таким id не найден"));
        User user = userRepository.findUserById(fromUserId)
                .orElseThrow(()-> new EntityNotFoundException("Пользователь  с таким id не найден"));
        if (friend.getFriends().contains(user.getId())) {
            user.getFriends().add(friend.getId());
            friendRepository.addFriend(fromUserId,toFriendId, RequestStatus.CONFIRMED);
        } else {
            friend.getFriends().add(fromUserId);
            friendRepository.addFriend(fromUserId,toFriendId,RequestStatus.NOT_CONFIRMED);
        }
        log.info("Added friend {} to user {}", fromUserId, toFriendId);
    }

    public void deleteFriend(long userId, long friendId) {
        log.info("Delete friend {} from user {}", friendId, userId);
        getUserOrException(userId).getFriends().remove(friendId);
        getUserOrException(friendId).getFriends().remove(userId);
        log.info("Deleted friend {} from user {}", friendId, userId);
    }

   /* public Collection<User> getCommonFriends(long userId, long otherId) {
        log.info("Get common friends {} from user {}", otherId, userId);
        Set<Long> commonIds = getUserOrException(otherId)
                .getFriends()
                .stream()
                .filter(id -> getUserOrException(userId).getFriends().contains(id))
                .collect(Collectors.toSet());
        return commonIds.stream().map(this::getUserOrException).toList();
    }*/

    public List<User> getUserFriends(long id) {
        log.info("Getting friends from user id {}",id);
        List<User> friends = friendRepository.getUserFriends(id);
        if (friends.isEmpty()) {
            throw new EntityNotFoundException("Друзей не надено");
        }
        return friends;
    }

    private void checkIfNameIsBlank(NewUserRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            log.info("Name null ? -> Name = login");
            request.setName(request.getLogin());
        }
        log.trace("Name is not null");
    }

}
