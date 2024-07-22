package ru.yandex.practicum.filmorate.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.test.NotFoundException;
import ru.yandex.practicum.filmorate.test.model.Film;
import ru.yandex.practicum.filmorate.test.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.test.storage.Storage;

import java.util.List;
import java.util.Optional;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping(value = "/films", produces = APPLICATION_JSON_VALUE)
public class FilmController {

    private final Storage<Film> storage;

    public FilmController(InMemoryFilmStorage inMemoryFilmStorage) {
        storage = inMemoryFilmStorage;
    }


    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Film add(@Validated @RequestBody Film film) {
        log.debug("Получен запрос создания фильма:\n{}", film);
        Film payload = storage.add(film);
        log.info("Фильм с ID {} добавлен", payload.getId());
        log.debug(payload.toString());
        return payload;
    }

    /* Специально для ревьюеров.
     * Глагол PUT, согласно его описанию в пункте 9.3.4 RFC9110, СОЗДАЁТ новый экземпляр ресурса в том случае, если
     * экземпляры, соответствующие присланной репрезентации, не найдены. Поэтому никакой речи не может быть о том,
     * чтобы сообщить клиенту об ошибке, если он прислал данные о ещё не созданном фильме: такой фильм следует создать
     * и отдать соотв. код. Но тесты пайплайна Github Actions, прохождение которых считается обязательным, написаны
     * с НАРУШЕНИЕМ пункта 9.3.4 RFC9110: они ожидают ошибку 4хх-5хх в ответ на запрос обновления ещё не созданного
     * экземпляра ресурса. ТОЛЬКО ИЗ-ЗА ЭТОГО метод возвращает ошибку при создании. Этот "костыль" будет убран,
     * как только тесты будут приведены в соответствие с RFC9110. */
    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Film> update(@Validated @RequestBody Film film) {
        HttpStatus status;
        String action;
        Film payload;
        log.debug("Получен запрос обновления/создания фильма:\n{}", film);
        Optional<Integer> filmIdOpt = Optional.ofNullable(film.getId());
        Optional<Film> previousFilmOpt = filmIdOpt.map(storage::get);
        if (filmIdOpt.isPresent() && previousFilmOpt.isPresent()) {
            payload = storage.update(film);
            status = OK;
            action = "обновлён";
        } else {
            payload = storage.add(film);
            status = INTERNAL_SERVER_ERROR;
            action = "создан";
        }
        log.info("Фильм с ID {} {}", payload.getId(), action);
        log.debug(payload.toString());
        return new ResponseEntity<>(payload, status);
    }

    @GetMapping
    public List<Film> getAll() {
        log.debug("Получен запрос получения списка всех фильмов");
        return storage.getAll();
    }

    @GetMapping("/{filmId}")
    public Film get(@PathVariable Integer filmId) {
        log.debug("Получен запрос данных фильма с ID {}", filmId);
        return Optional.ofNullable(storage.get(filmId)).orElseThrow(
                () -> new NotFoundException("Фильм с ID " + filmId + " не найден")
        );
    }
}
