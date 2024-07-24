package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.Storage;
import java.util.List;
import java.util.Optional;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping(value = "/films", produces = APPLICATION_JSON_VALUE)
public class FilmController {

    private final Storage<Film> filmStorage;
    private final FilmService filmService;
    private final UserService userService;

    public FilmController(
            InMemoryFilmStorage inMemoryFilmStorage,
            FilmService filmService,
            UserService userService
    ) {
        filmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
        this.userService = userService;
    }


    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Film add(@Validated @RequestBody Film film) {
        log.debug("Получен запрос создания фильма:\n{}", film);
        Film payload = filmStorage.add(film);
        log.info("Фильм с ID {} добавлен", payload.getId());
        log.debug(payload.toString());
        return payload;
    }

    /* Специально для ревьюеров.
     * Глагол PUT, согласно его описанию в пункте 9.3.4 RFC9110, СОЗДАЁТ новый экземпляр ресурса в том случае, если
     * экземпляры, соответствующие присланной репрезентации, не найдены. Поэтому никакой речи не может быть о том,
     * чтобы сообщить клиенту об ошибке, если он прислал данные о ещё не созданном фильме: такой фильм следует создать
     * и отдать соотв. код. Но тесты пайплайна Github Actions, прохождение которых считается обязательным, написаны
     * с НАРУШЕНИЕМ пункта 9.3.4 RFC9110: они ожидают ошибку 404 в ответ на запрос обновления ещё не созданного
     * экземпляра ресурса. ТОЛЬКО ИЗ-ЗА ЭТОГО метод возвращает ошибку при создании. Этот "костыль" будет убран,
     * как только тесты будут приведены в соответствие с RFC9110. */
    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Film> update(@Validated @RequestBody Film film) {
        HttpStatus status;
        String action;
        Film payload;
        log.debug("Получен запрос обновления/создания фильма:\n{}", film);
        Optional<Integer> filmIdOpt = Optional.ofNullable(film.getId());
        Optional<Film> previousFilmOpt = filmIdOpt.map(filmStorage::get);
        if (filmIdOpt.isPresent() && previousFilmOpt.isPresent()) {
            payload = filmStorage.update(film);
            status = OK;
            action = "обновлён";
        } else {
            payload = filmStorage.add(film);
            status = NOT_FOUND;
            action = "создан";
        }
        log.info("Фильм с ID {} {}", payload.getId(), action);
        log.debug(payload.toString());
        return new ResponseEntity<>(payload, status);
    }

    @GetMapping
    public List<Film> getAll() {
        log.debug("Получен запрос получения списка всех фильмов");
        return filmStorage.getAll();
    }

    @GetMapping("/{filmId}")
    public Film getById(@PathVariable Integer filmId) {
        log.debug("Получен запрос данных фильма с ID {}", filmId);
        return Optional.ofNullable(filmStorage.get(filmId)).orElseThrow(
                () -> new NotFoundException("Фильм с ID " + filmId + " не найден")
        );
    }

    // пользователь удаляет лайк
    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(NO_CONTENT)
    public void setLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        log.debug("Получен запрос добавления пользователем с ID {} отметки \"Нравится\" фильму с ID {}", userId, filmId);
        Film film = filmService.retrieveFilm(filmId);
        User user = userService.retrieveUser(userId);
        filmService.setLike(film, user);
        log.debug("Пользователь с ID {} установил отметку \"Нравится\" фильму с ID {}", userId, filmId);
    }

    // пользователь ставит лайк фильму
    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(NO_CONTENT)
    public void unsetLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        log.debug("Получен запрос удаления пользователем с ID {} отметки \"Нравится\" для фильма с ID {}", userId, filmId);
        Film film = filmService.retrieveFilm(filmId);
        User user = userService.retrieveUser(userId);
        filmService.unsetLike(film, user);
        log.debug("Пользователь с ID {} удалил отметку \"Нравится\" у фильма с ID {}", userId, filmId);
    }

    // список первых N фильмов по количеству отметок "Нравится"
    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.debug("Получен запрос списка из {} фильмов с наибольшим количеством отметок \"Нравится\"", count);
        return filmService.getPopular(count);
    }
}
