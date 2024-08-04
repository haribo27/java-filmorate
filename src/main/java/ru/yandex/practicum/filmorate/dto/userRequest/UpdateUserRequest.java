package ru.yandex.practicum.filmorate.dto.userRequest;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private long id;
    private String email;
    private String name;
    private String login;
    private LocalDate birthday;

    public boolean hasEmail() {
        return email.isBlank() || email.isEmpty();
    }

    public boolean hasName() {
        return name.isEmpty() || name.isBlank();
    }

    public boolean hasBirthday() {
        return birthday == null;
    }

    public boolean hasLogin() {
        return login.isBlank() || login.isEmpty();
    }
}
