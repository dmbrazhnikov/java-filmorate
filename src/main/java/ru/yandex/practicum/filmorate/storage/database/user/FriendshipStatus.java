package ru.yandex.practicum.filmorate.storage.database.user;

import jakarta.persistence.*;


@Entity
@Table(name = "friendship_status")
public class FriendshipStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
}
