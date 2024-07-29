package ru.yandex.practicum.filmorate.validation;

public record FieldErrorDTO(String objectName, String field, String message) {}