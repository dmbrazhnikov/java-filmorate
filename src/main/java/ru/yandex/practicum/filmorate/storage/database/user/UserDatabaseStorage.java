package ru.yandex.practicum.filmorate.storage.database.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.IUserStorage;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Component
@Transactional
public class UserDatabaseStorage implements IUserStorage {

    private final UserDaoRepository userDaoRepo;

    @Override
    public void add(User user) {
        UserDao incomingUserDao = UserDao.builder()
                .login(user.getLogin())
                .name(Optional.ofNullable(user.getName()).orElse(user.getLogin()))
                .email(user.getEmail())
                .birthDate(user.getBirthday())
                .build();
        UserDao savedUserDao = userDaoRepo.save(incomingUserDao);
        user.setId(savedUserDao.getId());
    }

    @Override
    public void update(User user) {
        if (!userDaoRepo.existsById(user.getId()))
            // Костыль: тест, нарушая RFC9110, ожидает 404 в ответ на попытку обновить несуществующий фильм
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден");
        UserDao incomingUserDao = UserDao.builder()
                .login(user.getLogin())
                .name(Optional.ofNullable(user.getName()).orElse(user.getLogin()))
                .email(user.getEmail())
                .birthDate(user.getBirthday())
                .build();
        userDaoRepo.save(incomingUserDao);
    }

    @Override
    public User get(Long userId) {
        UserDao dao = userDaoRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с ID " + userId + " не найден")
        );
        return getUserByDao(dao);
    }

    @Override
    public List<User> getAll() {
        return userDaoRepo.findAll().stream()
                .map(UserDatabaseStorage::getUserByDao)
                .toList();
    }

    @Override
    public void setFriendship(Long userId, Long friendUserId) {
        
        // TODO Реализация
    }

    @Override
    public void unsetFriendship(Long userId, Long friendUserId) {
        // TODO Реализация
    }

    @Override
    public Set<Long> getUserFriendsIds(Long userId) {
        return Set.of();
    }

    private static User getUserByDao(UserDao userDao) {
        return User.builder()
                .id(userDao.getId())
                .login(userDao.getLogin())
                .name(userDao.getName())
                .email(userDao.getEmail())
                .birthday(userDao.getBirthDate())
                .build();
    }
}
