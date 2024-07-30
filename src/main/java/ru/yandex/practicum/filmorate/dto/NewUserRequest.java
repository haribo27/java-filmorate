package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class NewUserRequest {

    @NotBlank
    @Pattern(regexp = "^\\S*$")
    private String login;
    private String name;
    @Email
    @NotEmpty
    private String email;
    @PastOrPresent
    @NotNull
    private LocalDate birthday;
    private Set<Long> friends;
}
