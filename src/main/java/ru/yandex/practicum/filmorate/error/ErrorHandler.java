package ru.yandex.practicum.filmorate.error;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.InternalServerErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;

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

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(final InternalServerErrorException e) {
        log.error("Ошибка сервера.", e);
        return new ErrorResponse("Ошибка сервера.", e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameter(final ConstraintViolationException e) {
        log.error("Ошибка валидации.", e);
        return new ErrorResponse("Ошибка валидации.", e.getMessage());
    }

    @ExceptionHandler(InvocationTargetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameter(final InvocationTargetException e) {
        log.error("Ошибка валидации.", e);
        return new ErrorResponse("Ошибка валидации.", e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectParameter(final NoSuchElementException e) {
        log.error("Ошибка с входным параметром.", e);
        return new ErrorResponse("Ошибка с входным параметром.", e.getMessage());
    }
}