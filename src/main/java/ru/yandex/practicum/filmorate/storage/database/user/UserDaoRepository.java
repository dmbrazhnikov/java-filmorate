package ru.yandex.practicum.filmorate.storage.database.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDaoRepository extends JpaRepository<UserDao, Long> {
}
