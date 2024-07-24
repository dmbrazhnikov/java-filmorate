package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.validation.ErrorDTO;
import java.util.List;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorDTO processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        ErrorDTO error = new ErrorDTO("ошибка валидации");
        fieldErrors.forEach(fieldError -> error.add(fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage()));
        return error;
    }

    @ExceptionHandler(NullValueException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorDTO processNullValueException(NullValueException e) {
        log.error("Пустое значение поля {} недопустимо", e.getFieldWithNullValueName());
        return new ErrorDTO("Пустое значение поля " + e.getFieldWithNullValueName() + " недопустимо");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorDTO processAnyException(Exception e) {
        log.error("Возникло исключение", e);
        return new ErrorDTO("Возникло исключение", e.getMessage());
    }
}
