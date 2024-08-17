package ru.yandex.practicum.filmorate.dto.userRequest;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {

    @Email
    @NotEmpty
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
}
