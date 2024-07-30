package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User mapToUser(NewUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());
        user.setLogin(request.getLogin());
        user.setFriends(request.getFriends());
        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setBirthday(user.getBirthday());
        dto.setLogin(user.getLogin());
        //dto.setFriends(user.getFriends());
        return dto;
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        if (!request.hasName()) {
            user.setName(request.getName());
        }
        if (!request.hasBirthday()) {
            user.setBirthday(request.getBirthday());
        }
        if (!request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        if (!request.hasLogin()) {
            user.setLogin(request.getLogin());
        }
        return user;
    }
}
