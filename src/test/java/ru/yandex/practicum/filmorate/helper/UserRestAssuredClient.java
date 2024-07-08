package ru.yandex.practicum.filmorate.helper;


public class UserRestAssuredClient extends BaseRestAssuredClient {

    public UserRestAssuredClient() {
        super("/users");
    }
}
