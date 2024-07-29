package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;


public interface IUserService {

    User add(User user);

    User update(User user);

    User get(Integer userId);

    List<User> getAll();

    void setFriendship(Integer userId, Integer friendUserId);

    void unsetFriendship(Integer userId, Integer friendUserId);

    List<User> getMutualFriends(Integer user1Id, Integer user2Id);

    List<User> getUserFriends(Integer userId);
}
