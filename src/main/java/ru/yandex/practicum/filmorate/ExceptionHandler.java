package ru.yandex.practicum.filmorate;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.validation.ErrorDTO;

import java.util.List;


@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        ErrorDTO error = new ErrorDTO("ошибка валидации");
        fieldErrors.forEach(fieldError -> error.add(fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage()));
        return error;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(FixYourCrookedTestException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO processCrookedTestException(FixYourCrookedTestException ex) {
        return ErrorDTO.builder()
                .message("ошибка теста")
                .description(ex.getMessage())
                .build();
    }
}
