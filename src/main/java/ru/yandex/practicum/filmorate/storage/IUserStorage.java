package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Set;


public interface IUserStorage {

    void add(User user);

    void update(User user);

    User get(Integer userId);

    List<User> getAll();

    void setFriendship(Integer userId, Integer friendUserId);

    void unsetFriendship(Integer userId, Integer friendUserId);

    Set<Integer> getUserFriendsIds(Integer userId);
}
