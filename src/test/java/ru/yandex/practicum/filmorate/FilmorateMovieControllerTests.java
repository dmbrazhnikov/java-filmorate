package ru.yandex.practicum.filmorate;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.model.Movie;
import java.time.Duration;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;


@DisplayName("Фильм")
@SpringBootTest(classes = FilmorateApplication.class, webEnvironment = RANDOM_PORT)
class FilmorateMovieControllerTests {

	private static final MovieRestClient movieRestClient = new MovieRestClient();
	private static final Movie refMovie = Movie.builder()
			.name("Молчание ягнят")
			.duration(Duration.ofMinutes(118))
			.description("Хороший, годный фильм")
			.releaseDate(LocalDate.of(1991, 2, 14))
			.build();

	@LocalServerPort
	int port;

	@BeforeEach
	void before() {
		RestAssured.port = port;
	}

	@Test
	@DisplayName("Добавление с корректными атрибутами")
	void addValid() {
		movieRestClient.sendCreateMovieRequest(refMovie)
				.then()
				.statusCode(CREATED.value())
				.and()
				.assertThat().body("id", notNullValue(Integer.class));
	}

	@DisplayName("Добавление с одним некорректным атрибутом")
	@ParameterizedTest(name = "{0}")
	@MethodSource({"provideMoviesWithSingleNonValidAttribute"})
	void badRequest(Movie movie, String errorMessage) {
		movieRestClient.sendCreateMovieRequest(movie)
				.then()
				.statusCode(BAD_REQUEST.value())
				.and()
				.assertThat().body("fieldErrors[0].message", equalToIgnoringCase(errorMessage));
	}

	@DisplayName("Получение всех")
	@Test
	void getAllMovies() {
		Movie anotherMovie = Movie.builder()
				.name("Things we lost in the fire")
				.duration(Duration.ofMinutes(118))
				.description("Лучшая роль Бенисио Дель Торо!")
				.releaseDate(LocalDate.of(2007, 10, 19))
				.build();
		movieRestClient.sendCreateMovieRequest(anotherMovie);
		movieRestClient.sendGetAllMoviesRequest()
				.then()
				.statusCode(OK.value())
				.and()
				.assertThat().body("size()", greaterThan(1));
	}

	@DisplayName("Обновление")
	@Test
	void update() {
		int movieId = movieRestClient.sendCreateMovieRequest(refMovie).path("id");
		Movie updatedMovie = refMovie.toBuilder().id(movieId).description("Джоди Фостер необычайно хороша!").build();
		movieRestClient.sendPutMovieRequest(updatedMovie)
				.then()
				.statusCode(NO_CONTENT.value());
	}

	private static Stream<Arguments> provideMoviesWithSingleNonValidAttribute() {
		return Stream.of(
				arguments(named("Название null", refMovie.toBuilder().name(null).build()),
						"название не может быть пустым"),
				arguments(named("Пустое название", refMovie.toBuilder().name("").build()),
						"название не может быть пустым"),
				arguments(named("Превышение длины описания",
								refMovie.toBuilder()
										.description(new String(new char[201]).replace('\0', 'a'))
										.build()),
						"описание должно содержать не более 200 символов"),
				arguments(named("Дата релиза ранее разрешённой",
								refMovie.toBuilder()
										.releaseDate(LocalDate.of(1895, 12, 27))
										.build()),
						"дата должна быть позже 1895-12-28"),
				arguments(named("Недостаточная длительность",
								refMovie.toBuilder().duration(Duration.ofMinutes(10)).build()),
						"длительность должна превышать 30 минут")
		);
	}
}
