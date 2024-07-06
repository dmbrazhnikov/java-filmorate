package ru.yandex.practicum.filmorate.helper;

import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;

public abstract class BaseRestAssuredClient {

    protected final String URL_PREFIX;
    protected static RestAssuredConfig config;

    public BaseRestAssuredClient(String urlPrefix) {
        URL_PREFIX = urlPrefix;
        config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout", 10000)
                        .setParam("http.connection.timeout", 10000));
    }
}
