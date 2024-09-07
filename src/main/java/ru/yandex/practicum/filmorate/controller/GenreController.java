package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.database.GenreRepository;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {

    /* Специально для ревьюеров.
    Работа с репозиторием сделана напрямую преднамеренно, чтобы не преступать принцип "бритвы Оккама" и не плодить
    бессмысленные сервисные классы-прослойки ради двух элементарнейших эндпойнтов, не требующих никакой спецлогики. */
    private final GenreRepository repo;

    @GetMapping
    public List<Genre> getAll() {
        log.debug("Получен запрос получения списка всех жанров");
        List<Genre> result = repo.findAll();
        log.info("Отправлен список всех жанров");
        return result;
    }

    @GetMapping("/{genreId}")
    public Genre getById(@PathVariable Long genreId) {
        log.debug("Получен запрос данных жанра с ID {}", genreId);
        Genre result = repo.findById(genreId).orElseThrow(
                () -> new NotFoundException("Жанр с ID " + genreId + " не найден")
        );
        log.info("Найден жанр с ID {}", genreId);
        return result;
    }
}
