package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;


@Component
public class InMemoryUserStorage implements IUserStorage {

    private static final Map<Long, User> usersByUserId = new HashMap<>();
    private static final Map<Long, Set<Long>> friendsIdsByUserId = new HashMap<>();

    @Override
    public void add(User user) {
        usersByUserId.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        usersByUserId.put(user.getId(), user);
    }

    @Override
    public User get(Long userId) {
        return usersByUserId.get(userId);
    }

    @Override
    public List<User> getAll() {
        return usersByUserId.values().stream().toList();
    }

    @Override
    public void setFriendship(Long userId, Long friendUserId) {
        Set<Long> userFriendIds = friendsIdsByUserId.computeIfAbsent(userId, v -> new HashSet<>()),
                friendUserFriendIds = friendsIdsByUserId.computeIfAbsent(friendUserId, v -> new HashSet<>());
        userFriendIds.add(friendUserId);
        friendUserFriendIds.add(userId);
        friendsIdsByUserId.put(userId, userFriendIds);
        friendsIdsByUserId.put(friendUserId, friendUserFriendIds);
    }

    @Override
    public void unsetFriendship(Long userId, Long friendUserId) {
        Set<Long> userFriendIds = friendsIdsByUserId.get(userId),
                friendUserFriendIds = friendsIdsByUserId.get(friendUserId);
        if (userFriendIds != null && friendUserFriendIds != null) {
            userFriendIds.remove(friendUserId);
            friendUserFriendIds.remove(userId);
        }
    }

    @Override
    public Set<Long> getUserFriendsIds(Long userId) {
        return Optional.ofNullable(friendsIdsByUserId.get(userId)).orElse(new HashSet<>());
    }
}
