package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    public ItemDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testSerializeItemDtoToJson() throws Exception {
        // Создание тестового объекта ItemDto
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(1);
        itemDto.setRequestId(2);

        // Сериализация объекта в JSON
        String json = objectMapper.writeValueAsString(itemDto);

        // Проверка содержимого JSON
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item Name\"");
        assertThat(json).contains("\"description\":\"Item Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"ownerId\":1");
        assertThat(json).contains("\"requestId\":2");
    }

    @Test
    void testDeserializeJsonToItemDto() throws Exception {
        // Пример JSON для десериализации
        String json = "{\"id\": 1, \"name\": \"Item Name\", \"description\": \"Item Description\", \"available\": true, \"ownerId\": 1, \"requestId\": 2}";


        // Десериализация JSON в объект ItemDto
        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        // Проверка полей десериализованного объекта
        assertThat(itemDto.getId()).isEqualTo(1);
        assertThat(itemDto.getName()).isEqualTo("Item Name");
        assertThat(itemDto.getDescription()).isEqualTo("Item Description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getOwnerId()).isEqualTo(1);
        assertThat(itemDto.getRequestId()).isEqualTo(2);
    }

    @Test
    void testValidationFailsWhenFieldsAreBlank() {
        // Создание объекта с пустыми полями, которые должны быть заполнены
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(true);

        // Выполнение валидации с использованием PostValidationGroup
        Set<jakarta.validation.ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, PostValidationGroup.class);

        // Проверка, что валидация не прошла из-за пустых полей name и description
        assertThat(violations).hasSize(2);
    }

    @Test
    void testValidationPassesForValidItemDto() {
        // Создание корректного объекта
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Valid Item");
        itemDto.setDescription("Valid Description");
        itemDto.setAvailable(true);

        // Выполнение валидации с использованием PostValidationGroup
        Set<jakarta.validation.ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, PostValidationGroup.class);

        // Проверка, что валидация успешно прошла
        assertThat(violations).isEmpty();
    }
}
