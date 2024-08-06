package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private Long id;
    private String email;
    private String name;
    private String login;
    private LocalDate birthday;
}
