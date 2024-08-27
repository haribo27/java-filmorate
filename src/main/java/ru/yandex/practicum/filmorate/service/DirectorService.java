package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorStorage;
import ru.yandex.practicum.filmorate.dal.FilmDirectorRepository;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.directorRequest.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.directorRequest.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {

    private final DirectorStorage directorRepository;
    private final FilmDirectorRepository filmDirectorRepository;

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

    public void isDirectorExist(long id) {
        log.debug("Check director exist with id {}", id);
        directorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Директор с таким id=%d не существует", id)));
    }

    public void isDirectorExist(Set<DirectorDto> directors) {
        if (directors == null) return;
        log.info("Check if directors exists: {}", directors);
        try {
            directors.forEach(director -> getDirector(director.getId()));
        } catch (EntityNotFoundException e) {
            throw new ValidationException("Директора с таким айди не существует");
        }
    }

    public void saveFilmsDirector(long id, Set<Director> directors) {
        filmDirectorRepository.saveDirector(id, directors.stream().toList());
    }

    public void updateDirector(long id, Set<Director> directors) {
        filmDirectorRepository.deleteFilmsDirector(id);
        saveFilmsDirector(id, directors);
    }
}
