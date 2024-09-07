package ru.yandex.practicum.filmorate.storage.database.user;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;


@Converter(autoApply = true)
public class FriendshipStatusConverter implements AttributeConverter<FriendshipStatus, String> {

    @Override
    public String convertToDatabaseColumn(FriendshipStatus status) {
        if (status == null)
            return null;
        return status.getName();
    }

    public FriendshipStatus convertToEntityAttribute(String statusName) {
        if (statusName == null)
            return null;
        return Stream.of(FriendshipStatus.values())
                .filter(s -> s.getName().equals(statusName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Некорректное значение статуса дружбы: " + statusName));
    }
}
