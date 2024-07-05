package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
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
@RequestMapping(value = "/movie", produces = APPLICATION_JSON_VALUE)
public class MovieController {

    private static final Map<Integer, Movie> moviesById = new ConcurrentHashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger();

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public Movie add(@RequestBody Movie movie) {
        int movieId = idSequence.getAndIncrement();
        movie.setId(movieId);
        moviesById.put(movieId, movie);
        log.info("Фильм с ID {} добавлен", movieId);
        log.debug(movie.toString());
        return movie;
    }

    @PutMapping(value = "/{movieId}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void update(@RequestBody Movie movie, @PathVariable Integer movieId) {
        moviesById.put(movie.getId(), movie);
        log.info("Фильм с ID {} обновлён", movieId);
    }

    @GetMapping("/all")
    public List<Movie> getAll() {
        return moviesById.values().stream().toList();
    }
}
