package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@Slf4j
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<GenreDto> getAllGenres() {
        log.info("Get all genres");
        return genreRepository.getGenres()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .toList();
    }

    public GenreDto getGenreById(long id) {
        log.info("Get genre by id: {}", id);
        return genreRepository.getGenreById(id)
                .map(GenreMapper::mapToGenreDto)
                .orElseThrow(() -> new ValidationException("Жанра с таким id не существует"));
    }

    public void isGenresExists(LinkedHashSet<Genre> genres) {
        log.info("Check if genres exists: {}", genres);
        genres.forEach(genre -> getGenreById(genre.getId()));
    }
}
