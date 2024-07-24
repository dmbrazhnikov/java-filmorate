package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users", produces = APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public User add(@Validated @RequestBody User user) {
        log.debug("Получен запрос создания пользователя:\n{}", user);
        User payload = userService.add(user);
        log.info("Пользователь с ID {} добавлен", payload.getId());
        log.debug(payload.toString());
        return payload;
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    public User update(@Validated @RequestBody User user) {
        log.debug("Получен запрос обновления/создания пользователя:\n{}", user);
        User payload = userService.update(user);
        log.info("Пользователь с ID {} обновлён", payload.getId());
        log.debug(payload.toString());
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        log.debug("Получен запрос получения списка всех пользователей");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Integer userId) {
        log.debug("Получен запрос данных пользователя с ID {}", userId);
        return userService.get(userId);
    }

    // добавление в друзья
    @PutMapping("/{userId}/friends/{friendUserId}")
    @ResponseStatus(NO_CONTENT)
    public void setFriendship(@PathVariable Integer userId, @PathVariable Integer friendUserId) {
        log.debug("Получен запрос добавления в друзья пользователя с ID {} от пользователя с ID {}", userId, friendUserId);
        User user = userService.get(userId), friendUser = userService.get(friendUserId);
        userService.setFriendship(user, friendUser);
        log.info("Пользователь с ID {} добавлен в друзья пользователя с ID {}", friendUserId, userId);
    }

    // удаление из друзей
    @DeleteMapping("/{userId}/friends/{friendUserId}")
    @ResponseStatus(NO_CONTENT)
    public void unsetFriendship(@PathVariable Integer userId, @PathVariable Integer friendUserId) {
        log.debug("Получен запрос удаления из друзей пользователя с ID {} от пользователя с ID {}", userId, friendUserId);
        User user = userService.get(userId), friendUser = userService.get(friendUserId);
        userService.unsetFriendship(user, friendUser);
        log.info("Пользователь с ID {} удалён из друзей пользователя с ID {}", friendUserId, userId);
    }

    // список друзей пользователя
    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable Integer userId) {
        // TODO Перенести логику в севрис
        User user = userService.get(userId);
        return user.getFriends().stream()
                .map(storage::get)
                .collect(Collectors.toList());
    }

    // Общие для двух пользователей друзья
    // common - некорректное в данном случае слово
    // https://translate.google.com/?sl=ru&tl=en&text=%D0%BE%D0%B1%D1%89%D0%B8%D0%B5%20%D0%B4%D1%80%D1%83%D0%B7%D1%8C%D1%8F&op=translate
    @GetMapping("/{userId}/friends/common/{friendUserId}")
    public List<User> getMutualFriends(@PathVariable Integer userId, @PathVariable Integer friendUserId) {
        // TODO Перенести логику в севрис
        User user = userService.retrieveUser(userId), friendUser = userService.retrieveUser(friendUserId);
        return userService.mutualFriendIds(user, friendUser).stream()
                .map(storage::get)
                .collect(Collectors.toList());
    }
}
