package ru.yandex.practicum.filmorate.dto.reviewRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewReviewRequest {

    @NotNull
    @NotBlank
    @Size(max = 5000)
    private String content;
    @NotNull
    private boolean isPositive;
    @NotNull
    private long userId;
    @NotNull
    private long filmId;
    @NotNull
    private int useful;
}
