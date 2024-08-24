package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.directorRequest.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.directorRequest.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DirectorService {

    private final DirectorStorage directorRepository;

    public DirectorDto createDirector(NewDirectorRequest request) {
        log.info("Creating director: {}", request);
        Director director = DirectorMapper.mapToDirector(request);
        log.info("Mapped director: {}", director);
        director = directorRepository.createDirector(director);
        log.info("Created new director: {}", director);
        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto updateDirector(UpdateDirectorRequest request) {
        log.info("Updating director: {}", request);
        Director updatedDirector = directorRepository.findById(request.getId())
                .map(director -> DirectorMapper.updateDirectorFields(director, request))
                .orElseThrow(() -> new EntityNotFoundException("Director not found"));
        directorRepository.updateDirector(updatedDirector);
        log.info("Director updated: {}", updatedDirector);
        return DirectorMapper.mapToDirectorDto(updatedDirector);
    }

    public void deleteDirector(long id) {
        log.trace("Deleting director with id: {}", id);
        isDirectorExist(id);
        directorRepository.deleteDirector(id);
        log.info("Director deleted with id: {}", id);
    }

    public List<DirectorDto> getAllDirectors() {
        log.info("Getting all directors");
        List<DirectorDto> directorDtos = directorRepository.getAllDirector()
                .stream()
                .map(DirectorMapper::mapToDirectorDto)
                .toList();
        log.info("Коллекция Директоров успешно передана.");
        return directorDtos;
    }

    public DirectorDto getDirector(long id) {
        log.info("Попытка получить Директора с id={}.", id);
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Директор с таким id=%d не существует", id)));
        log.info("Getting director : {}", director);
        return DirectorMapper.mapToDirectorDto(director);
    }

    private void isDirectorExist(long id) {
        log.debug("Check director exist with id {}", id);
        directorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Директор с таким id=%d не существует", id)));
    }

    public void isDirectorExist(Set<Director> directors) {
        if (directors == null) return;
        log.info("Check if directors exists: {}", directors);
        try {
            directors.forEach(director -> getDirector(director.getId()));
        } catch (EntityNotFoundException e) {
            throw new ValidationException("Директора с таким айди не существует");
        }
    }
    // Получаем список id всех режиссеров
    public Collection<Long> getAllDirectorsIds() {
        return directorRepository.getAllDirector().stream().map(Director::getId).collect(Collectors.toList());
    }

    // Получаем список режиссеров по id фильма
    public Collection<Director> getAllDirectorsByFilmId(Long id) {
        return directorRepository.findAllByFilmId(id).stream().toList();
    }

    // Добавляем режиссера к фильму в сводную таблицу
    public void addDirectorToFilm(long id, Set<Director> directors) {
        for (Director director : directors) {
            directorRepository.insertIntoFilmDirector(id, director.getId());
        }
    }
}
