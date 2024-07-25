package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullValueException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@RequiredArgsConstructor
public class UserService implements EntityService<User, Integer> {

    private final InMemoryUserStorage storage;
    private static final AtomicInteger idSequence = new AtomicInteger(1);

    /* При создании значение поля User.id обязано быть пустым: управлять идентификаторами может исключительно сервер,
    а значит, клиент обязан прислать данные без идентификатора. Поэтому бездумно вешать валидацию на null нельзя. */
    @Override
    public User add(User user) {
        int userId = idSequence.getAndIncrement();
        user.setId(userId);
        storage.add(user);
        return user;
    }

    /* При обновлении поле User.id должно быть заполнено. Для негативного результата проверки этого условия и сделано
    * отдельное исключение по аналогии с NotFoundException. */
    @Override
    public User update(User user) {
        if (user.getId() == null)
            throw new NullValueException("ru.yandex.practicum.filmorate.model.User.id");
        get(user.getId()); // Костыль: тест, в нарушение RFC9110, ожидает 404 в ответ на попытку обновить несуществующего пользователя
        storage.update(user);
        return user;
    }

    @Override
    public User get(Integer id) {
        return Optional.ofNullable(storage.get(id)).orElseThrow(
                () -> new NotFoundException("Пользователь с ID " + id + " не найден")
        );
    }

    @Override
    public List<User> getAll() {
        return storage.getAll();
    }

    // добавление в друзья
    public void setFriendship(User user, User friendUser) {
        Set<Integer> userFriendsIds = storage.getUserFriendsIds(user.getId()),
                friendUserFriendsIds = storage.getUserFriendsIds(friendUser.getId());
        userFriendsIds.add(friendUser.getId());
        friendUserFriendsIds.add(user.getId());
        storage.setUserFriendIds(user.getId(), userFriendsIds);
        storage.setUserFriendIds(friendUser.getId(), friendUserFriendsIds);
    }

    // удаление из друзей
    public void unsetFriendship(User user, User friendUser) {
        Set<Integer> userFriendsIds = storage.getUserFriendsIds(user.getId()),
                friendUserFriendsIds = storage.getUserFriendsIds(friendUser.getId());
        if (!userFriendsIds.isEmpty() && !friendUserFriendsIds.isEmpty()) {
            userFriendsIds.remove(friendUser.getId());
            storage.setUserFriendIds(user.getId(), userFriendsIds);
            friendUserFriendsIds.remove(user.getId());
            storage.setUserFriendIds(friendUser.getId(), friendUserFriendsIds);
        }
    }

    // вывод списка общих друзей
    public List<User> getMutualFriends(User user1, User user2) {
        Set<Integer> user1FriendsIds = storage.getUserFriendsIds(user1.getId()),
                user2FriendsIds = storage.getUserFriendsIds(user2.getId());
        if (!user1FriendsIds.isEmpty() && !user2FriendsIds.isEmpty()) {
            user1FriendsIds.retainAll(user2FriendsIds);
        }
        return user1FriendsIds.stream()
                .map(storage::get)
                .toList();
    }

    public List<User> getUserFriends(User user) {
        return storage.getUserFriendsIds(user.getId()).stream()
                .map(storage::get)
                .toList();
    }
}
