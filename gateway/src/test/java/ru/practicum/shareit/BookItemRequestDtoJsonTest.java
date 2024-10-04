package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    public BookItemRequestDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testSerializeBookItemRequestDtoToJson() throws Exception {
        // Создание тестового объекта
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(
                1L,
                LocalDateTime.of(2024, 10, 1, 12, 0),
                LocalDateTime.of(2024, 10, 2, 12, 0)
        );

        // Сериализация объекта в JSON
        String json = objectMapper.writeValueAsString(bookItemRequestDto);

        // Проверка содержимого JSON
        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"start\":\"2024-10-01T12:00:00\"");
        assertThat(json).contains("\"end\":\"2024-10-02T12:00:00\"");
    }

    @Test
    void testDeserializeJsonToBookItemRequestDto() throws Exception {
        // Пример JSON для десериализации
        String json = "{"
                + "\"itemId\": 1,"
                + "\"start\": \"2024-10-01T12:00:00\","
                + "\"end\": \"2024-10-02T12:00:00\""
                + "}";


        // Десериализация JSON в объект
        BookItemRequestDto bookItemRequestDto = objectMapper.readValue(json, BookItemRequestDto.class);

        // Проверка полей десериализованного объекта
        assertThat(bookItemRequestDto.getItemId()).isEqualTo(1L);
        assertThat(bookItemRequestDto.getStart()).isEqualTo(LocalDateTime.of(2024, 10, 1, 12, 0));
        assertThat(bookItemRequestDto.getEnd()).isEqualTo(LocalDateTime.of(2024, 10, 2, 12, 0));
    }

    @Test
    void testValidationFailsWhenStartIsInThePast() {
        // Создание объекта с неправильной датой начала (в прошлом)
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(
                1L,
                LocalDateTime.of(2023, 10, 1, 12, 0),  // В прошлом
                LocalDateTime.of(2025, 10, 20, 12, 0)
        );

        // Выполнение валидации
        Set<jakarta.validation.ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookItemRequestDto);

        // Проверка, что валидация не прошла
        assertThat(violations).hasSize(1);
    }
}
