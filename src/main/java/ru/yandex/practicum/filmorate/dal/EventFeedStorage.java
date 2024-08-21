package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.EventFeed;

import java.util.List;

public interface EventFeedStorage {

    List<EventFeed> getByUserId(int userId);

    void save(EventFeed event);
}