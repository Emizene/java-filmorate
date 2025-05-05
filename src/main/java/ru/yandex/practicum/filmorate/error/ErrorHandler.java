package ru.yandex.practicum.filmorate.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
@RestControllerAdvice
@SuppressWarnings("unused")
public class ErrorHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameter(final ValidationException e) {
        log.error("Ошибка валидации.", e);
        return new ErrorResponse("Ошибка валидации.", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Ошибка с входным параметром.", e);
        return new ErrorResponse("Ошибка с входным параметром.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(final Exception e) {
        log.error("Ошибка сервера.", e);
        return new ErrorResponse("Ошибка сервера.", e.getMessage());
    }
}