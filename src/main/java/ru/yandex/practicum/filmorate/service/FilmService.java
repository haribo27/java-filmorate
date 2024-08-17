package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.filmRequest.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.filmRequest.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.FilmStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;


    public FilmService(FilmStorage filmRepository, FilmGenreRepository filmGenreRepository, UserService userService, MpaService mpaService, GenreService genreService) {
        this.filmRepository = filmRepository;
        this.filmGenreRepository = filmGenreRepository;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public FilmDto createFilm(NewFilmRequest request) {
        log.info("Creating film: {}", request);
        mpaService.isMpaExist(request.getMpa().getId());
        genreService.isGenresExists(request.getGenres());
        Film film = FilmMapper.mapToFilm(request);
        film = filmRepository.createFilm(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenreRepository.saveGenre(film.getId(), film.getGenres().stream().toList());
        }
        log.info("Created new film: {}", film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
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
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильма с таким id не существует"));
        log.info("Getting film : {}", film);
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
        return filmRepository.getPopularFilms(count);
    }

    private void isFilmExist(long id) {
        log.debug("Check film exist with id {}", id);
        filmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм в данным айди не найден"));
    }
}
