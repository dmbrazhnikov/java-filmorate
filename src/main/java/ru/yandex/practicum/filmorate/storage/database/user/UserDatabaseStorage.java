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

import static ru.yandex.practicum.filmorate.storage.database.user.FriendshipStatus.CONFIRMED;
import static ru.yandex.practicum.filmorate.storage.database.user.FriendshipStatus.REQUESTED;


@RequiredArgsConstructor
@Component
@Transactional
public class UserDatabaseStorage implements IUserStorage {

    private final UserDaoRepository userDaoRepo;
    private final UserFriendshipRepository userFriendshipRepo;

    @Override
    public void add(User user) {
        UserDao newUserDao = UserDao.builder()
                .login(user.getLogin())
                .name(Optional.ofNullable(user.getName()).orElse(user.getLogin()))
                .email(user.getEmail())
                .birthDate(user.getBirthday())
                .build();
        UserDao savedUserDao = userDaoRepo.save(newUserDao);
        user.setId(savedUserDao.getId());
    }

    @Override
    public void update(User user) {
        if (!userDaoRepo.existsById(user.getId()))
            // Костыль: тест, нарушая RFC9110, ожидает 404 в ответ на попытку обновить несуществующий фильм
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден");
        UserDao updatedUserDao = UserDao.builder()
                .id(user.getId())
                .login(user.getLogin())
                .name(Optional.ofNullable(user.getName()).orElse(user.getLogin()))
                .email(user.getEmail())
                .birthDate(user.getBirthday())
                .build();
        userDaoRepo.save(updatedUserDao);
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
    public void requestFriendship(Long userId, Long friendUserId) {
        UsersFriendship newUserFriendShip = UsersFriendship.builder()
                .userId(userId)
                .friendUserId(friendUserId)
                .friendshipStatus(REQUESTED)
                .build();
        userFriendshipRepo.save(newUserFriendShip);
    }

    @Override
    public void confirmFriendship(Long userId, Long friendUserId) {
        UsersFriendship userFriendShip = Optional.ofNullable(
                userFriendshipRepo.findFirstByUserIdAndFriendUserIdAndFriendshipStatus(userId, friendUserId, REQUESTED)
        ).orElseThrow(
                () -> new NotFoundException("Пользователь с ID " + friendUserId
                        + " не запрашивал добавление в друзья пользователя с ID " + userId)
        );
        userFriendShip.setFriendshipStatus(CONFIRMED);
        userFriendshipRepo.save(userFriendShip);
    }

    @Override
    public void unsetFriendship(Long userId, Long friendUserId) {
        UsersFriendship userFriendShip = userFriendshipRepo.findFirstByUserIdAndFriendUserId(userId, friendUserId);
        if (userFriendShip != null)
            userFriendshipRepo.delete(userFriendShip);
    }

    @Override
    public Set<Long> getUserFriendsIds(Long userId) {
        // TODO Реализация
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
