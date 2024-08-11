package ru.yandex.practicum.filmorate.e2e;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;


@DisplayName("Фильм")
@SpringBootTest(classes = FilmorateApplication.class, webEnvironment = RANDOM_PORT)
class FilmTests {

	private static final RestAssuredClient filmClient = new RestAssuredClient("/films");
	private static Film refFilm;

	@LocalServerPort
	int port;

	@BeforeEach
	void beforeEach() {
		RestAssured.port = port;
		refFilm = getRefFilm();
	}

	/* Некоторые проверки преднамеренно упрощены (неполны) для ускорения разработки */

	@Test
	@DisplayName("Добавление с корректными атрибутами")
	void addValid() {
		filmClient.sendPost(refFilm)
				.then()
				.statusCode(CREATED.value())
				.and()
				.assertThat().body("id", notNullValue(Integer.class));
	}

	@DisplayName("Добавление с одним некорректным атрибутом")
	@ParameterizedTest(name = "{0}")
	@MethodSource("provideMoviesWithSingleNonValidAttribute")
	void badRequest(Film film, String errorMessage) {
		filmClient.sendPost(film)
				.then()
				.statusCode(BAD_REQUEST.value())
				.and()
				.assertThat().body("fieldErrors[0].message", equalToIgnoringCase(errorMessage));
	}

	@DisplayName("Получение всех")
	@Test
	void getAllMovies() {
		Film anotherFilm = Film.builder()
				.name("Things we lost in the fire")
				.durationMinutes(118)
				.description("Лучшая роль Бенисио Дель Торо!")
				.releaseDate(LocalDate.of(2007, 10, 19))
				.build();
		filmClient.sendPost(anotherFilm);
		filmClient.sendGet("")
				.then()
				.statusCode(OK.value())
				.and()
				.assertThat().body("size()", greaterThan(1));
	}

	@DisplayName("Обновление")
	@Test
	void update() {
		Long filmId = filmClient.sendPost(refFilm).path("id");
		String newDesc = "Джоди Фостер необычайно хороша!";
		Film updatedFilm = refFilm.toBuilder()
				.id(filmId)
				.description(newDesc)
				.build();
		filmClient.sendPutWithPayload("", updatedFilm)
				.then()
				.statusCode(OK.value())
				.and()
				.assertThat().body("description", equalTo(newDesc));
	}

	@DisplayName("Получение по ID существующего")
	@Test
	void getExistingById() {
		Long filmId = filmClient.sendPost(refFilm).path("id");
		refFilm.setId(filmId);
		Film result = filmClient.sendGet("/" + filmId)
				.then()
				.statusCode(OK.value())
				.extract()
				.as(Film.class);
		assertThat(result).isEqualTo(refFilm);
	}

	@Test
	@DisplayName("Получение по ID несуществующего")
	void getNonExistingById() {
		filmClient.sendGet("/" + 9999)
				.then()
				.statusCode(NOT_FOUND.value());
	}

	private static Stream<Arguments> provideMoviesWithSingleNonValidAttribute() {
		refFilm = getRefFilm();
		return Stream.of(
				arguments(named("Название null", refFilm.toBuilder().name(null).build()),
						"название не может быть пустым"),
				arguments(named("Пустое название", refFilm.toBuilder().name("").build()),
						"название не может быть пустым"),
				arguments(named("Превышение длины описания",
								refFilm.toBuilder()
										.description(new String(new char[201]).replace('\0', 'a'))
										.build()),
						"описание должно содержать не более 200 символов"),
				arguments(named("Дата релиза ранее разрешённой",
								refFilm.toBuilder()
										.releaseDate(LocalDate.of(1895, 12, 27))
										.build()),
						"дата должна быть позже 1895-12-28")
		);
	}

	private static Film getRefFilm() {
		return Film.builder()
				.name("Молчание ягнят")
				.description("111")
				.durationMinutes(118)
				.releaseDate(LocalDate.of(1991, 2, 14))
				.build();
	}
}
