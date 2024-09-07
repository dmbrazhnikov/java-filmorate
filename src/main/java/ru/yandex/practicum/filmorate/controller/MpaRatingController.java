package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.database.MpaRatingRepository;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MpaRatingController {

    /* Специально для ревьюеров.
    Работа с репозиторием сделана напрямую преднамеренно, чтобы не преступать принцип "бритвы/метлы Оккама" и не плодить
    бессмысленные сервисные классы-прослойки ради двух элементарнейших эндпойнтов, не требующих никакой спецлогики. */
    private final MpaRatingRepository repo;

    @GetMapping
    public List<MpaRating> getAll() {
        log.debug("Получен запрос получения списка всех жанров");
        List<MpaRating> result = repo.findAll();
        log.info("Отправлен список всех жанров");
        return result;
    }

    @GetMapping("/{ratingId}")
    public MpaRating getById(@PathVariable Long ratingId) {
        log.debug("Получен запрос данных рейтинга с ID {}", ratingId);
        MpaRating result = repo.findById(ratingId).orElseThrow(
                () -> new NotFoundException("Рейтинг с ID " + ratingId + " не найден")
        );
        log.info("Найден рейтинг с ID {}", ratingId);
        return result;
    }
}
