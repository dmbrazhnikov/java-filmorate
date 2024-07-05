package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping(value = "/users", produces = APPLICATION_JSON_VALUE)
public class UserController {

    private static final Map<Integer, User> usersById = new ConcurrentHashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger();

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public User add(@RequestBody User user) {
        int userId = idSequence.getAndIncrement();
        user.setId(userId);
        usersById.put(userId, user);
        log.info("Пользователь с ID {} добавлен", userId);
        log.debug(user.toString());
        return user;
    }

    @PutMapping(value = "/{userId}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void update(@RequestBody User user, @PathVariable Integer userId) {
        usersById.put(userId, user);
        log.info("Пользователь с ID {} обновлён", userId);
    }

    @GetMapping("/all")
    public List<User> getAll() {
        return usersById.values().stream().toList();
    }
}
