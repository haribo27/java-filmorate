package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre implements Comparable<Genre> {

    private long id;
    private String name;

    @Override
    public int compareTo(Genre other) {
        return Long.compare(this.id, other.id);
    }
}
