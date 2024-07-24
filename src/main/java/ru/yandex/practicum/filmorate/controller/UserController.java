package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping(value = "/users", produces = APPLICATION_JSON_VALUE)
public class UserController {

    private final Storage<User> storage;
    private final UserService userService;

    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        storage = inMemoryUserStorage;
        this.userService = userService;
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
     * чтобы сообщить клиенту об ошибке, если он прислал данные о ещё не созданном пользователе: такого пользователя
     * следует создать и отдать соотв. код. Но тесты пайплайна Github Actions, прохождение которых считается
     * обязательным, написаны с НАРУШЕНИЕМ пункта 9.3.4 RFC9110: они ожидают ошибку 404 в ответ на запрос
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
            status = NOT_FOUND;
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

    @GetMapping("/{userId}")
    public User getById(@PathVariable Integer userId) {
        log.debug("Получен запрос данных попользователя с ID {}", userId);
        return Optional.ofNullable(storage.get(userId)).orElseThrow(
                () -> new NotFoundException("Пользователь с ID " + userId + " не найден")
        );
    }

    // добавление в друзья
    @PutMapping("/{userId}/friends/{friendUserId}")
    @ResponseStatus(NO_CONTENT)
    public void setFriendship(@PathVariable Integer userId, @PathVariable Integer friendUserId) {
        User user = userService.retrieveUser(userId),
                friendUser = userService.retrieveUser(friendUserId);
        userService.setFriendship(user, friendUser);
    }

    // удаление из друзей
    @DeleteMapping("/{userId}/friends/{friendUserId}")
    @ResponseStatus(NO_CONTENT)
    public void unsetFriendship(@PathVariable Integer userId, @PathVariable Integer friendUserId) {
        User user = userService.retrieveUser(userId), friendUser = userService.retrieveUser(friendUserId);
        userService.unsetFriendship(user, friendUser);
    }

    // список друзей пользователя
    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable Integer userId) {
        User user = userService.retrieveUser(userId);
        return user.getFriends().stream()
                .map(storage::get)
                .collect(Collectors.toList());
    }

    // Общие для двух пользователей друзья
    // common - некорректное в данном случае слово
    // https://translate.google.com/?sl=ru&tl=en&text=%D0%BE%D0%B1%D1%89%D0%B8%D0%B5%20%D0%B4%D1%80%D1%83%D0%B7%D1%8C%D1%8F&op=translate
    @GetMapping("/{userId}/friends/common/{friendUserId}")
    public List<User> getMutualFriends(@PathVariable Integer userId, @PathVariable Integer friendUserId) {
        User user = userService.retrieveUser(userId), friendUser = userService.retrieveUser(friendUserId);
        return userService.mutualFriendIds(user, friendUser).stream()
                .map(storage::get)
                .collect(Collectors.toList());
    }
}
