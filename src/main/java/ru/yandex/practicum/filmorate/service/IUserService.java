package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;


public interface IUserService {

    User add(User user);

    User update(User user);

    User get(Long userId);

    List<User> getAll();

    void setFriendship(Long userId, Long friendUserId);

    void unsetFriendship(Long userId, Long friendUserId);

    List<User> getMutualFriends(Long user1Id, Long user2Id);

    List<User> getUserFriends(Long userId);
}
