package ru.yandex.practicum.filmorate.dto.directorRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewDirectorRequest {

    private long id;
    @NotBlank
    @Size(max = 100)
    private String name;
}
