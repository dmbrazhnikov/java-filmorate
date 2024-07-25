package ru.yandex.practicum.filmorate.e2e;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;


public class BaseTest {

    private static final AtomicInteger userSeq = new AtomicInteger(1);

    protected static User getTestUser() {
        int id = userSeq.getAndIncrement();
        return User.builder()
                .login("user" + id)
                .name("Тестовый пользователь " + id)
                .email("user" + id + "@server.com")
                .birthday(LocalDate.of(
                        ThreadLocalRandom.current().nextInt(1970, 2001),
                        ThreadLocalRandom.current().nextInt(1, 13),
                        ThreadLocalRandom.current().nextInt(1, 29))
                ).build();
    }
}
