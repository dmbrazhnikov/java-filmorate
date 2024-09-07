package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.IUserStorage;
import ru.yandex.practicum.filmorate.storage.database.user.UserDatabaseStorage;
import java.util.List;


@Service
public class UserServiceImpl implements IUserService {

    private final IUserStorage userStorage;

    public UserServiceImpl(UserDatabaseStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User add(User user) {
        userStorage.add(user);
        return user;
    }

    @Override
    public User update(User user) {
        userStorage.update(user);
        return user;
    }

    @Override
    public User get(Long id) {
        return userStorage.get(id);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    // добавление в друзья
    @Override
    public void setFriendship(Long userId, Long friendUserId) {
        User user = get(userId), friendUser = get(friendUserId);
        userStorage.requestFriendship(user.getId(), friendUser.getId());
    }

    // удаление из друзей
    @Override
    public void unsetFriendship(Long userId, Long friendUserId) {
        User user = get(userId), friendUser = get(friendUserId);
        userStorage.unsetFriendship(user.getId(), friendUser.getId());
    }

    // вывод списка общих друзей
    @Override
    public List<User> getMutualFriends(Long user1Id, Long user2Id) {
        User user1 = get(user1Id), user2 = get(user2Id);
        List<Long> user1FriendsIds = userStorage.getUserFriendsIds(user1.getId()),
                user2FriendsIds = userStorage.getUserFriendsIds(user2.getId());
        if (!user1FriendsIds.isEmpty() && !user2FriendsIds.isEmpty())
            user1FriendsIds.retainAll(user2FriendsIds);
        return user1FriendsIds.stream()
                .map(userStorage::get)
                .toList();
    }

    @Override
    public List<User> getUserFriends(Long userId) {
        User user = get(userId);
        return userStorage.getUserFriendsIds(user.getId()).stream()
                .map(userStorage::get)
                .toList();
    }
}
