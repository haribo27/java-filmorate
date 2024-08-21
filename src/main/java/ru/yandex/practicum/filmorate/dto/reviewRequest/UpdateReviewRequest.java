package ru.yandex.practicum.filmorate.dto.reviewRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateReviewRequest {

    private long reviewId;
    @NotNull
    @NotBlank
    @Size(max = 5000)
    private String content;
    @NotNull
    private Boolean isPositive;
    private long userId;
    private long filmId;
    private int useful;

}
