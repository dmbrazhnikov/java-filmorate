package ru.yandex.practicum.filmorate.helper;


public class FilmRestAssuredClient extends BaseRestAssuredClient {

    public FilmRestAssuredClient() {
        super("/films");
    }
}
