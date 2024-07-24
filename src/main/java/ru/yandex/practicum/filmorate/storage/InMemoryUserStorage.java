package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class InMemoryUserStorage implements Storage<User, Integer> {

    private static final Map<Integer, User> usersById = new ConcurrentHashMap<>();

    @Override
    public void add(User user) {
        usersById.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        usersById.put(user.getId(), user);
    }

    @Override
    public User get(Integer userId) {
        return usersById.get(userId);
    }

    @Override
    public List<User> getAll() {
        return usersById.values().stream().toList();
    }
}
