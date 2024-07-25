package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;


@Component
public class InMemoryUserStorage implements Storage<User, Integer> {

    private static final Map<Integer, User> usersByUserId = new HashMap<>();
    private static final Map<Integer, Set<Integer>> friendsIdsByUserId = new HashMap<>();

    @Override
    public void add(User user) {
        usersByUserId.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        usersByUserId.put(user.getId(), user);
    }

    @Override
    public User get(Integer userId) {
        return usersByUserId.get(userId);
    }

    @Override
    public List<User> getAll() {
        return usersByUserId.values().stream().toList();
    }

    public Set<Integer> getUserFriendsIds(Integer userId) {
        return Optional.ofNullable(friendsIdsByUserId.get(userId)).orElse(new HashSet<>());
    }

    public void setUserFriendIds(Integer userId, Set<Integer> friendsIds) {
        friendsIdsByUserId.put(userId, friendsIds);
    }
}
