package ru.yandex.practicum.filmorate.storage.database.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FriendshipStatus {

    REQUESTED("запрошена"), CONFIRMED("подтверждена");

    private final String name;
}