package ru.yandex.practicum.filmorate.service;

import java.util.List;

public interface EntityService<E, I> {

    E add(E entity);

    E update(E entity);

    E get(I id);

    List<E> getAll();
}
