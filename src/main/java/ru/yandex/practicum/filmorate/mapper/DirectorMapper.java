package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.directorRequest.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectorMapper {
    public static Director mapToDirector(UpdateDirectorRequest request) {
        Director director = new Director();
        director.setName(request.getName());
        return director;
    }

    public static DirectorDto mapToDirectorDto(Director director) {
        DirectorDto directorDto = new DirectorDto();
        directorDto.setId(director.getId());
        directorDto.setName(director.getName());
        return directorDto;
    }

    public static Director updateDirectorFields(Director director, UpdateDirectorRequest request) {
        if (!request.hasName()) {
            director.setName(request.getName());
        }
        return director;
    }
}
