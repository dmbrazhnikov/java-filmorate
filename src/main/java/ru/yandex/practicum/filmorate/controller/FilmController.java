package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping(value = "/films", produces = APPLICATION_JSON_VALUE)
public class FilmController {

    private static final Map<Integer, Film> moviesById = new ConcurrentHashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger();

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Film add(@Validated @RequestBody Film film) {
        int movieId = idSequence.getAndIncrement();
        film.setId(movieId);
        moviesById.put(movieId, film);
        log.info("Фильм с ID {} добавлен", movieId);
        log.debug(film.toString());
        return film;
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    public Film update(@Validated @RequestBody Film film) {
        Integer movieId = film.getId();
        moviesById.put(movieId, film);
        log.info("Фильм с ID {} обновлён", movieId);
        log.debug(film.toString());
        return film;
    }

    @GetMapping()
    public List<Film> getAll() {
        return moviesById.values().stream().toList();
    }
}
