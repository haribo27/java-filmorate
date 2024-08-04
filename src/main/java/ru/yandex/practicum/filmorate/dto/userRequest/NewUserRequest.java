package ru.yandex.practicum.filmorate.dto.userRequest;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {

    @Email
    @NotEmpty
    @NotNull
    private String email;
    private String name;
    @NotBlank
    @Pattern(regexp = "^\\S*$")
    private String login;
    @PastOrPresent
    @NotNull
    private LocalDate birthday;
}
