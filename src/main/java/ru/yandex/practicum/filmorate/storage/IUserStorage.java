package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Set;


public interface IUserStorage {

    void add(User user);

    void update(User user);

    User get(Long userId);

    List<User> getAll();

    void requestFriendship(Long userId, Long friendUserId);

    void confirmFriendship(Long userId, Long friendUserId);

    void unsetFriendship(Long userId, Long friendUserId);

    Set<Long> getUserFriendsIds(Long userId);
}
