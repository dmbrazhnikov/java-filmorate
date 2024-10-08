package ru.yandex.practicum.filmorate.e2e;

import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.fail;


public class RestAssuredClient {

    protected final String uriPrefix;
    protected static RestAssuredConfig config;

    public RestAssuredClient(String uriPrefix) {
        this.uriPrefix = uriPrefix;
        config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout", 10000)
                        .setParam("http.connection.timeout", 10000));
    }

    public <T> Response sendPost(String uriSuffix, T payload) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .contentType(JSON)
                    .accept(JSON)
                    .body(payload)
                    .post(uriPrefix + uriSuffix);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }

    public <T> Response sendPost(T payload) {
        return sendPost("", payload);
    }

    public <T> Response sendPutWithPayload(String uriSuffix, T payload) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .contentType(JSON)
                    .body(payload)
                    .put(uriPrefix + uriSuffix);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }

    public <T> Response sendPutWithPayload(T payload) {
        return sendPutWithPayload("", payload);
    }

    public Response sendPutWithoutPayload(String uriSuffix) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .put(uriPrefix + uriSuffix);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }

    public Response sendGet(String uriSuffix) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .accept(JSON)
                    .get(uriPrefix + uriSuffix);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }

    public Response sendDelete(String uriSuffix) {
        Response response = null;
        try {
            response = given()
                    .config(config)
                    .delete(uriPrefix + uriSuffix);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getClass() + " " + e.getMessage());
        }
        return response;
    }
}
