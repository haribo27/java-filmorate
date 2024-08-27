package ru.yandex.practicum.filmorate.dto.userRequest;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private long id;
    @Email
    @Size(max = 100)
    private String email;
    @Size(max = 100)
    private String name;
    @NotBlank
    @Pattern(regexp = "^\\S*$")
    @Size(max = 100)
    private String login;
    @PastOrPresent
    @NotNull
    private LocalDate birthday;

    public boolean hasEmail() {
        return email == null || email.isBlank();
    }

    public boolean hasName() {
        return name == null || name.isBlank();
    }

    public boolean hasBirthday() {
        return birthday == null;
    }

    public boolean hasLogin() {
        return login == null || login.isBlank();
    }
}
