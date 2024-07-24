package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullValueException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class UserService implements EntityService<User, Integer> {

    private final Storage<User, Integer> storage;
    private static final AtomicInteger idSequence = new AtomicInteger(1);

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        storage = inMemoryUserStorage;
    }

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

    //добавление в друзья
    public void setFriendship(User user1, User user2) {
        // TODO Перенести хранение в хранилище
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
    }

    //удаление из друзей
    public void unsetFriendship(User user1, User user2) {
        // TODO Перенести хранение в хранилище
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
    }

    // вывод списка общих друзей
    public Set<Integer> mutualFriendIds(User baseUser, User userToCompareWith) {
        // TODO Перенести хранение в хранилище
        Set<Integer> result = new HashSet<>(baseUser.getFriends());
        result.retainAll(userToCompareWith.getFriends());
        return result;
    }
}
