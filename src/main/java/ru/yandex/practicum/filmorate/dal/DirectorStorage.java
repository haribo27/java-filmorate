package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(long id);

    List<Director> getAllDirector();

    Optional<Director> findById(long id);

    Collection<Director> findAllByFilmId(Long id);

    void insertIntoFilmDirector(long id, long directorId);
}

