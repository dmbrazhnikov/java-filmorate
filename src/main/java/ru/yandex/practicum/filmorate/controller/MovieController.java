package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Movie;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping(value = "/films", produces = APPLICATION_JSON_VALUE)
public class MovieController {

    private static final Map<Integer, Movie> moviesById = new ConcurrentHashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger();

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Movie add(@Validated @RequestBody Movie movie) {
        int movieId = idSequence.getAndIncrement();
        movie.setId(movieId);
        moviesById.put(movieId, movie);
        log.info("Фильм с ID {} добавлен", movieId);
        log.debug(movie.toString());
        return movie;
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    public Movie update(@Validated @RequestBody Movie movie) {
        Integer movieId = movie.getId();
        moviesById.put(movieId, movie);
        log.info("Фильм с ID {} обновлён", movieId);
        log.debug(movie.toString());
        return movie;
    }

    @GetMapping()
    public List<Movie> getAll() {
        return moviesById.values().stream().toList();
    }
}
