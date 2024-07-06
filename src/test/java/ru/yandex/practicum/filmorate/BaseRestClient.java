package ru.yandex.practicum.filmorate;

import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;


public class BaseRestClient {

    protected static RestAssuredConfig config;

    public BaseRestClient() {
        config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout", 10000)
                        .setParam("http.connection.timeout", 10000));
    }
}
