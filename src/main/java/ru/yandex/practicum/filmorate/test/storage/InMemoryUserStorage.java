package ru.yandex.practicum.filmorate.test.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.test.model.User;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class InMemoryUserStorage implements Storage<User> {

    private static final Map<Integer, User> usersById = new ConcurrentHashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);

    @Override
    public User add(User user) {
        int userId = idSequence.getAndIncrement();
        user.setId(userId);
        usersById.put(userId, user);
        return user;
    }

    @Override
    public User update(User user) {
        usersById.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(int userId) {
        return usersById.get(userId);
    }

    @Override
    public List<User> getAll() {
        return usersById.values().stream().toList();
    }
}
