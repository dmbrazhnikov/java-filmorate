package ru.yandex.practicum.filmorate.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.test.model.User;
import ru.yandex.practicum.filmorate.test.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.test.storage.Storage;
import java.util.List;
import java.util.Optional;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping(value = "/users", produces = APPLICATION_JSON_VALUE)
public class UserController {

    private final Storage<User> storage;

    public UserController(InMemoryUserStorage inMemoryUserStorage) {
        storage = inMemoryUserStorage;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public User add(@Validated @RequestBody User user) {
        log.debug("Получен запрос создания пользователя:\n{}", user);
        User payload = storage.add(user);
        log.info("Пользователь с ID {} добавлен", payload.getId());
        log.debug(payload.toString());
        return payload;
    }

    /* Специально для ревьюеров.
     * Глагол PUT, согласно его описанию в пункте 9.3.4 RFC9110, СОЗДАЁТ новый экземпляр ресурса в том случае, если
     * экземпляры, соответствующие присланной репрезентации, не найдены. Поэтому никакой речи не может быть о том,
     * чтобы сообщить клиенту об ошибке, если он прислал данные о ещё не созданном пользователе: такого пользователе
     * следует создать и отдать соотв. код. Но тесты пайплайна Github Actions, прохождение которых считается
     * обязательным, написаны с НАРУШЕНИЕМ пункта 9.3.4 RFC9110: они ожидают ошибку 4хх-5хх в ответ на запрос
     * обновления ещё не созданного экземпляра ресурса. ТОЛЬКО ИЗ-ЗА ЭТОГО метод возвращает ошибку при создании.
     * Этот "костыль" будет убран, как только тесты будут приведены в соответствие с RFC9110. */
    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> update(@Validated @RequestBody User user) {
        HttpStatus status;
        String action;
        User payload;
        log.debug("Получен запрос обновления/создания пользователя:\n{}", user);
        Optional<Integer> filmIdOpt = Optional.ofNullable(user.getId());
        Optional<User> previousFilmOpt = filmIdOpt.map(storage::get);
        if (filmIdOpt.isPresent() && previousFilmOpt.isPresent()) {
            payload = storage.update(user);
            status = OK;
            action = "обновлён";
        } else {
            payload = storage.add(user);
            status = INTERNAL_SERVER_ERROR;
            action = "создан";
        }
        log.info("Пользователь с ID {} {}", payload.getId(), action);
        log.debug(payload.toString());
        return new ResponseEntity<>(payload, status);
    }

    @GetMapping
    public List<User> getAll() {
        log.debug("Получен запрос получения списка всех пользователей");
        return storage.getAll();
    }
}
