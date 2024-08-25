package ru.yandex.practicum.filmorate.storage.database.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "users_friendship")
public class UsersFriendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId, friendUserId;
    private Integer statusId;
    @Column(columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime lastUpdated;
}
