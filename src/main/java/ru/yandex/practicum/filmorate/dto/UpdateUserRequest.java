package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateUserRequest {

    private long id;
    @Email
    private String email;
    private String name;
    @Pattern(regexp = "^\\S*$")
    private String login;
    @PastOrPresent
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
        return login.isBlank()  || login.isEmpty();
    }
}
