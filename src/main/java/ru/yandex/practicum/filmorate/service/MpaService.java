package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;

import java.util.List;

@Service
@Slf4j
public class MpaService {

    private final MpaRepository mpaRepository;

    public MpaService(MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    public List<MpaDto> getAllMpa() {
        log.info("Get all ratings");
        return mpaRepository.getAllMpa()
                .stream()
                .map(MpaMapper::mapMpaToMpaDto)
                .toList();
    }

    public MpaDto getMpaById(long id) {
        log.info("Get ratings by id: {}", id);
        return mpaRepository.getMpaById(id)
                .map(MpaMapper::mapMpaToMpaDto)
                .orElseThrow(() -> new EntityNotFoundException("Рейтинг с таким id не найден"));
    }

    public void isMpaExist(long id) {
        log.info("Check if ratings exist with id: {}", id);
        mpaRepository.getMpaById(id)
                .orElseThrow(() -> new ValidationException("Рейтинга с таким айди не существует"));
    }
}
