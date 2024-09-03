package ru.yandex.practicum.filmorate.storage.database.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users_friendship")
public class UsersFriendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId, friendUserId;
    private FriendshipStatus friendshipStatus;
    @Column(columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime lastUpdated;
}
