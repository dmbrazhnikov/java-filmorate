package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.IUserStorage;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserStorage userStorage;
    private static final AtomicLong idSequence = new AtomicLong(1);

    /* При создании значение поля User.id обязано быть пустым: управлять идентификаторами может исключительно сервер,
    а значит, клиент обязан прислать данные без идентификатора. Поэтому бездумно вешать валидацию на null нельзя. */
    @Override
    public User add(User user) {
        Long userId = idSequence.getAndIncrement();
        user.setId(userId);
        userStorage.add(user);
        return user;
    }

    /* При обновлении поле User.id должно быть заполнено. Для негативного результата проверки этого условия и сделано
    * отдельное исключение по аналогии с NotFoundException. */
    @Override
    public User update(User user) {
        get(user.getId()); // Костыль: тест, в нарушение RFC9110, ожидает 404 в ответ на попытку обновить несуществующего пользователя
        userStorage.update(user);
        return user;
    }

    @Override
    public User get(Long id) {
        return Optional.ofNullable(userStorage.get(id)).orElseThrow(
                () -> new NotFoundException("Пользователь с ID " + id + " не найден")
        );
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    // добавление в друзья
    @Override
    public void setFriendship(Long userId, Long friendUserId) {
        User user = get(userId), friendUser = get(friendUserId);
        userStorage.setFriendship(user.getId(), friendUser.getId());
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
        Set<Long> user1FriendsIds = userStorage.getUserFriendsIds(user1.getId()),
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
