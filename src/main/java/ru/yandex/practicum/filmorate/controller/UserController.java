package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping(value = "/user", produces = APPLICATION_JSON_VALUE)
public class UserController {

    private static final Map<Integer, User> usersById = new ConcurrentHashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger();

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public User add(@Validated @RequestBody User user) {
        int userId = idSequence.getAndIncrement();
        user.setId(userId);
        usersById.put(userId, user);
        log.info("Пользователь с ID {} добавлен", userId);
        log.debug(user.toString());
        return user;
    }

    @PutMapping(value = "/{userId}", consumes = APPLICATION_JSON_VALUE)
    public User update(@Validated @RequestBody User user, @PathVariable Integer userId) {
        usersById.put(userId, user);
        log.info("Пользователь с ID {} обновлён", userId);
        log.debug(user.toString());
        return user;
    }

    @GetMapping("/all")
    public List<User> getAll() {
        return usersById.values().stream().toList();
    }
}
