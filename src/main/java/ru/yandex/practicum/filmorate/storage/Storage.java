package ru.yandex.practicum.filmorate.storage;

import java.util.List;


public interface Storage<E> {

    E add(E entity);

    E update(E entity);

    E get(int entityId);

    List<E> getAll();
}
