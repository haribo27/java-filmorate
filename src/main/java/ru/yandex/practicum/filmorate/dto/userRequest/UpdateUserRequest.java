package ru.yandex.practicum.filmorate.dto.userRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    @NotNull
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
