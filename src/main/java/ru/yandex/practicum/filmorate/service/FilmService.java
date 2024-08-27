package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.filmRequest.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.filmRequest.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.EventTypeFeed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.OperationFeed;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmRepository;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorService directorService;

    public List<FilmDto> getRecommendedFilms(long userId) {
        log.info("Getting recommended films to user {}", userId);
        return filmRepository.getRecommendedFilms(userId)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto createFilm(NewFilmRequest request) {
        log.info("Creating film: {}", request);
        mpaService.isMpaExist(request.getMpa().getId());
        genreService.isGenresExists(request.getGenres());
        directorService.isDirectorExist(request.getDirectors());
        Film film = FilmMapper.mapToFilm(request);
        film = filmRepository.createFilm(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreService.saveFilmsGenres(film.getId(), film.getGenres().stream().toList().reversed());
        }
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            directorService.saveFilmsDirector(film.getId(), film.getDirectors());
        }
        log.info("Created new film: {}", film);
        return filmRepository.findById(film.getId())
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new EntityNotFoundException("Film not found"));
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        log.info("Updating film: {}", request);
        Film updatedFilm = filmRepository.findById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new EntityNotFoundException("Film not found"));
        filmRepository.updateFilm(updatedFilm);
        genreService.updateGenres(updatedFilm.getId(), updatedFilm.getGenres().stream().toList());

        directorService.updateDirector(updatedFilm.getId(), updatedFilm.getDirectors());
        log.info("Film updated: {}", updatedFilm);
        return filmRepository.findById(updatedFilm.getId())
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new EntityNotFoundException("Film not Found"));
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
        userService.addEvent(userId, EventTypeFeed.LIKE, OperationFeed.ADD, filmId);
        isFilmExist(filmId);
        userService.getUserOrException(userId);
        if (!filmRepository.isLikeExist(userId, filmId)) {
            filmRepository.addFilmLike(userId, filmId);
        }
        log.info("Added like with id: {}, user id {}", filmId, userId);

    }

    public void removeFilmLike(long filmId, long userId) {
        log.trace("Deleting like from film {}, userId {}", filmId, userId);
        isFilmExist(filmId);
        userService.getUserOrException(userId);
        filmRepository.deleteLike(userId, filmId);
        userService.addEvent(userId, EventTypeFeed.LIKE, OperationFeed.REMOVE, filmId);
        log.info("Deleted like from film {}, user {}", filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
        log.info("Getting popular films");
        return filmRepository.getPopularFilms(count, genreId, year);
    }

    public List<FilmDto> getCommonFilms(long userId, long friendId) {
        log.info("GET /films/common?userId={}&friendId={}", userId, friendId);
        userService.isUsersExists(userId, friendId);
        return filmRepository.getCommonFilms(userId, friendId).stream()
                .map(FilmMapper::mapToFilmDto).toList();
    }

    private void isFilmExist(long id) {
        log.debug("Check film exist with id {}", id);
        filmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм в данным айди не найден"));
    }

    public List<FilmDto> getDirectorFilms(Long id, SortBy sortBy) {
        directorService.isDirectorExist(id);
        return filmRepository.getDirectorFilms(id, sortBy)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public List<FilmDto> search(String query, List<String> by) {
        return filmRepository.searchFilmsByParams(query, by)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .toList()
                .reversed();
    }
}
