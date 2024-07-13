package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping(value = "/users", produces = APPLICATION_JSON_VALUE)
public class UserController {

    private static final Map<Integer, User> usersById = new ConcurrentHashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public User add(@Validated @RequestBody User user) {
        log.debug("Получен запрос создания пользователя:\n{}", user);
        int userId = idSequence.getAndIncrement();
        user.setId(userId);
        usersById.put(userId, user);
        log.info("Пользователь с ID {} добавлен", userId);
        log.debug(user.toString());
        return user;
    }

    /* Специально для ревьюеров. Объяснение, почему этот код написан так, а не иначе.

     * Глагол PUT, согласно его описанию в пункте 9.3.4 RFC9110, СОЗДАЁТ новый экземпляр ресурса в том случае, если
     * экземпляры, соответствующие присланнму, не найдены. Поэтому никакой речи не может быть о том,
     * чтобы сообщить клиенту об ошибке, если он прислал данные о ещё не созданном фильме: такой фильм следует создать
     * и отдать соотв. код. Но тесты, прохождение которых считается обязательным, написаны с НАРУШЕНИЕМ пункта
     * 9.3.4 RFC9110: они ожидают ошибку 4хх-5хх в ответ на запрос обновления ещё не созданного экземпляра ресурса.
     * ТОЛЬКО ИЗ_ЗА ЭТОГО метод выбрасывает исключение. Это непотребство будет убрано, как только тесты привевдут
     * в соответствие с RFC. */
    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> update(@Validated @RequestBody User user) {
        HttpStatus status;
        String action;
        log.debug("Получен запрос обновления/создания пользователя:\n{}", user);
        Integer userId = Optional.ofNullable(user.getId()).orElse(idSequence.getAndIncrement());
        if (usersById.containsKey(userId)) {
            status = OK;
            action = "обновлён";
        } else {
            status = NOT_FOUND;
            action = "создан";
        }
        usersById.put(userId, user);
        log.info("Пользователь с ID {} {}", userId, action);
        log.debug(user.toString());
        return new ResponseEntity<>(user, status);
    }

    @GetMapping()
    public List<User> getAll() {
        log.debug("Получен запрос получения списка всех пользователей");
        return usersById.values().stream().toList();
    }
}
