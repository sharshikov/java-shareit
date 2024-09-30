package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerializeUserDtoToJson() throws Exception {
        // Создание тестового объекта UserDto
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        // Сериализация объекта в JSON
        String json = objectMapper.writeValueAsString(userDto);

        // Проверка содержимого JSON
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"John Doe\"");
        assertThat(json).contains("\"email\":\"john.doe@example.com\"");
    }

    @Test
    void testValidationFailsForInvalidEmail() {
        // Создание объекта с некорректным email
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("invalid-email");  // Некорректный email

        // Выполнение валидации
        Set<jakarta.validation.ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Проверка, что валидация не прошла из-за некорректного email
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("должно иметь формат адреса электронной почты");
    }

    @Test
    void testValidationPassesForValidUserDto() {
        // Создание корректного объекта
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        // Выполнение валидации
        Set<jakarta.validation.ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Проверка, что валидация успешно прошла
        assertThat(violations).isEmpty();
    }
}
