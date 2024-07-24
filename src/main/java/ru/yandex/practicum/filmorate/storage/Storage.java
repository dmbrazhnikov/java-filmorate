package ru.yandex.practicum.filmorate.storage;

import java.util.List;


public interface Storage<E, I> {
    void add(E entity);
    void update(E entity);
    E get(I entityId);
    List<E> getAll();
}
