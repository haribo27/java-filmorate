package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.filmRequest.FilmRequest;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.FilmStorage;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmRepository;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;


    public FilmService(@Qualifier("FilmRepo") FilmStorage filmRepository, UserService userService, MpaService mpaService, GenreService genreService) {
        this.filmRepository = filmRepository;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public FilmDto createFilm(FilmRequest request) {
        log.info("Creating film: {}", request);
        mpaService.isMpaExist(request.getMpa().getId());
        genreService.isGenresExists(request.getGenres());
        Film film = FilmMapper.mapToFilm(request);
        film = filmRepository.createFilm(film);
        log.info("Created new film: {}", film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(FilmRequest request) {
        log.info("Updating film: {}", request);
        Film updatedFilm = filmRepository.findById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new EntityNotFoundException("Film not found"));
        filmRepository.updateFilm(updatedFilm);
        log.info("Film updated: {}", updatedFilm);
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public void deleteFilm(long id) {
        log.trace("Deleting film with id: {}", id);
        isFilmExist(id);
        filmRepository.deleteFilm(id);
        log.info("Film deleted with id: {}", id);
    }

    public List<FilmDto> getAllFilms() {
        log.info("Getting all films");
        return filmRepository.getAllFilms()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto getFilm(long id) {
        log.trace("Getting film with id: {}", id);
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильма с таким id не существует"));
        return FilmMapper.mapToFilmDto(film);
    }

    public void addFilmLike(long filmId, long userId) {
        log.info("Adding like with film id: {}, user id {}", filmId, userId);
        filmRepository.findById(filmId).orElseThrow(() -> new EntityNotFoundException("Фильма с таким id не существует"));
        userService.getUserOrException(userId);
        filmRepository.addFilmLike(userId, filmId);
        log.info("Added like with id: {}, user id {}", filmId, userId);

    }

    public void removeFilmLike(long filmId, long userId) {
        log.trace("Deleting like from film {}, userId {}", filmId, userId);
        isFilmExist(filmId);
        userService.getUserOrException(userId);
        filmRepository.deleteLike(userId, filmId);
        log.info("Deleted like from film {}, user {}", filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Getting popular films");
        return filmRepository.getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film o) -> o.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private void isFilmExist(long id) {
        log.debug("Check film exist with id {}", id);
        filmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм в данным айди не найден"));
    }
}
