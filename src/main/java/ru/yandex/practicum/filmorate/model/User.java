package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {

    private Long id;
    @Email
    @NotEmpty
    private String email;
    private String name;
    @NotBlank
    @Pattern(regexp = "^\\S*$")
    private String login;
    @PastOrPresent
    @NotNull
    private LocalDate birthday;
    private Set<Long> friends;
}
