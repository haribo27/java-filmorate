package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

@Getter
public enum SortBy {
    YEAR("year"),
    LIKES("likes");

    private final String value;

    SortBy(String value) {
        this.value = value;
    }

    public static SortBy fromValue(String value) {
        for (SortBy sortBy : SortBy.values()) {
            if (sortBy.value.equalsIgnoreCase(value)) {
                return sortBy;
            }
        }
        throw new EntityNotFoundException(String.format("Sorted by %s not exist", value));
    }

}
