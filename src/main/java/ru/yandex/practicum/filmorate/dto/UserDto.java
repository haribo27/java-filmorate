package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDto {

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
