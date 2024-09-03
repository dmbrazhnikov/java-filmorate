package ru.yandex.practicum.filmorate.storage.database.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserFriendshipRepository extends JpaRepository<UsersFriendship, Long> {
    UsersFriendship findFirstByUserIdAndFriendUserIdAndFriendshipStatus(Long userId, Long friendUserId, FriendshipStatus status);
    UsersFriendship findFirstByUserIdAndFriendUserId(Long userId, Long friendUserId);
}