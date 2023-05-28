package ru.practicum.shareit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.*;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExceptionsHandlerTest {
    private final ExceptionsHandler handler = new ExceptionsHandler();

    @Test
    @DisplayName("Тест ошибки HttpStatus.NOT_FOUND")
    public void objectNotFoundExceptionTest() {
        ObjectNotFoundException e = new ObjectNotFoundException("object not found");
        ErrorResponse errorResponse = handler.objectNotFoundException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    @DisplayName("Тест ошибки валидации")
    public void validationExceptionTest() {
        ValidationException e = new ValidationException("validation error");
        ErrorResponse errorResponse = handler.validationException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    @DisplayName("Тест ошибки прав доступа для пользователя")
    public void wrongUserExceptionTest() {
        WrongUserException e = new WrongUserException("wrong user");
        ErrorResponse errorResponse = handler.wrongUserException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    @DisplayName("Тест ошибки имейла")
    public void emailConflictExceptionTest() {
        EmailConflictException e = new EmailConflictException("email error");
        ErrorResponse errorResponse = handler.emailConflictException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    @DisplayName("Тест ошибки HttpStatus.BAD_REQUEST")
    public void badRequestExceptionTest() {
        BadRequestException e = new BadRequestException("bad request");
        ErrorResponse errorResponse = handler.badRequestException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    @DisplayName("Тест ошибок runtime/HttpStatus.INTERNAL_SERVER_ERROR")
    public void runtimeExceptionTest() {
        RuntimeException e = new RuntimeException("other errors, including internal server error");
        ErrorResponse errorResponse = handler.runtimeException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }
}
