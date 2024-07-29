package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import jakarta.validation.groups.Default;
import ru.yandex.practicum.filmorate.validation.UpdateValidationGroup;
import java.util.List;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/films", produces = APPLICATION_JSON_VALUE)
public class FilmController {

    private final FilmServiceImpl filmService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Film add(@Validated @RequestBody Film film) {
        log.debug("Получен запрос создания фильма:\n{}", film);
        Film payload = filmService.add(film);
        log.info("Фильм с ID {} добавлен", payload.getId());
        return payload;
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    public Film update(@Validated({UpdateValidationGroup.class, Default.class}) @RequestBody Film film) {
        log.debug("Получен запрос обновления/создания фильма:\n{}", film);
        Film payload = filmService.update(film);
        log.info("Фильм с ID {} обновлён", payload.getId());
        return payload;
    }

    @GetMapping
    public List<Film> getAll() {
        log.debug("Получен запрос получения списка всех фильмов");
        List<Film> result = filmService.getAll();
        log.info("Отправлен список всех фильмов");
        return result;
    }

    @GetMapping("/{filmId}")
    public Film getById(@PathVariable Integer filmId) {
        log.debug("Получен запрос данных фильма с ID {}", filmId);
        Film result = filmService.get(filmId);
        log.info("Найден фильм с ID {}", filmId);
        return result;
    }

    // пользователь удаляет лайк
    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(NO_CONTENT)
    public void setLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        log.debug("Получен запрос добавления пользователем с ID {} отметки \"Нравится\" фильму с ID {}", userId, filmId);
        filmService.setLike(filmId, userId);
        log.debug("Пользователь с ID {} установил отметку \"Нравится\" фильму с ID {}", userId, filmId);
    }

    // пользователь ставит лайк фильму
    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(NO_CONTENT)
    public void unsetLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        log.debug("Получен запрос удаления пользователем с ID {} отметки \"Нравится\" для фильма с ID {}", userId, filmId);
        filmService.unsetLike(filmId, userId);
        log.debug("Пользователь с ID {} удалил отметку \"Нравится\" у фильма с ID {}", userId, filmId);
    }

    // список первых N фильмов по количеству отметок "Нравится"
    @GetMapping("/popular")
    public List<Film> getMostPopular(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.debug("Получен запрос списка из {} фильмов с наибольшим количеством отметок \"Нравится\"", count);
        return filmService.getMostPopular(count);
    }
}
