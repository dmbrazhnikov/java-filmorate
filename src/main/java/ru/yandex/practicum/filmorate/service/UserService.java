package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashSet;
import java.util.Set;


@Service
public class UserService {

    //добавление в друзья
    public void setFriendship(User user1, User user2) {
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
    }

    //удаление из друзей
    public void unsetFriendship(User user1, User user2) {
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
    }

    // вывод списка общих друзей
    public Set<Integer> mutualFriendIds(User baseUser, User userToCompareWith) {
        Set<Integer> result = new HashSet<>(baseUser.getFriends());
        result.retainAll(userToCompareWith.getFriends());
        return result;
    }
}
