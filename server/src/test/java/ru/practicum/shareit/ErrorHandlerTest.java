package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.handler.ErrorHandler;
import ru.practicum.shareit.response.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundDataException_shouldReturnNotFoundResponse() {
        // Подготовка
        NotFoundDataException exception = new NotFoundDataException("Resource not found");

        // Вызов метода
        ErrorResponse response = errorHandler.handleNotFoundDataException(exception);

        // Проверка
        assertEquals("Resource not found", response.getError());
    }

    @Test
    void handleDuplicateDataException_shouldReturnConflictResponse() {
        // Подготовка
        DuplicateDataException exception = new DuplicateDataException("Duplicate data");

        // Вызов метода
        ErrorResponse response = errorHandler.handleDuplicateDataException(exception);

        // Проверка
        assertEquals("Duplicate data", response.getError());
    }

    @Test
    void handleThrowable_shouldReturnInternalServerErrorResponse() {
        // Подготовка
        Throwable exception = new RuntimeException("Unexpected error");

        // Вызов метода
        ErrorResponse response = errorHandler.handleThrowable(exception);

        // Проверка
        assertEquals("Произошла непредвиденная ошибка.", response.getError());
    }
}
