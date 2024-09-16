package ru.yandex.practicum.filmorate.storage.database.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FilmDto;
import java.sql.ResultSet;
import java.sql.SQLException;


public class FilmRowMapper implements RowMapper<FilmDto> {

    @Override
    public FilmDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return FilmDto.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .durationMinutes(resultSet.getInt("duration_minutes"))
                .build();
    }
}
