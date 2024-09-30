package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
public class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private ItemDto testItemDto;
    private User testUser;

    @BeforeEach
    void setup() {
        // Создание тестового пользователя
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);

        // Создание DTO для тестового предмета
        testItemDto = new ItemDto();
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test Description");
        testItemDto.setAvailable(true);
    }

    @Test
    void whenCreateItem_thenItemIsCreated() {
        // Создание предмета через сервис
        ItemDto savedItem = itemService.createItem(testItemDto, testUser.getId());

        // Проверка, что предмет был успешно создан
        assertNotNull(savedItem.getId());
        assertEquals(testItemDto.getName(), savedItem.getName());
        assertEquals(testItemDto.getDescription(), savedItem.getDescription());
        assertEquals(testItemDto.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void whenGetItemById_thenItemIsReturned() {
        // Создание предмета через репозиторий
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(testUser);
        itemRepository.save(item);

        // Получение предмета через сервис
        ItemDto foundItem = itemService.getItemById(item.getId());

        // Проверка, что возвращенный предмет соответствует сохраненному
        assertNotNull(foundItem);
        assertEquals(item.getId(), foundItem.getId());
        assertEquals(item.getName(), foundItem.getName());
        assertEquals(item.getDescription(), foundItem.getDescription());
    }

    @Test
    void whenUpdateItem_thenItemIsUpdated() {
        // Создание и сохранение предмета
        Item item = new Item();
        item.setName("Old Item Name");
        item.setDescription("Old Description");
        item.setAvailable(false);
        item.setOwner(testUser);
        itemRepository.save(item);

        // DTO для обновления
        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setName("Updated Item Name");
        updateItemDto.setDescription("Updated Description");
        updateItemDto.setAvailable(true);

        // Обновление предмета через сервис
        ItemDto updatedItem = itemService.updateItem(testUser.getId(), item.getId(), updateItemDto);

        // Проверка, что данные были обновлены
        assertNotNull(updatedItem);
        assertEquals("Updated Item Name", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());
    }

    @Test
    void whenDeleteItem_thenItemIsDeleted() {
        // Создание и сохранение предмета
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(testUser);
        itemRepository.save(item);

        // Удаление предмета через сервис
        itemService.deleteItem(item.getId());

        // Проверка, что предмет был удален
        Optional<Item> deletedItem = itemRepository.findById(item.getId());
        assertFalse(deletedItem.isPresent());
    }

    @Test
    void whenGetAllItemsByUser_thenListOfItemsIsReturned() {
        // Создание и сохранение предметов
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(testUser);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(testUser);

        itemRepository.save(item1);
        itemRepository.save(item2);

        // Получение всех предметов пользователя через сервис
        var items = itemService.getAllItemsByUserId(testUser.getId());

        // Проверка, что список содержит созданные предметы
        assertEquals(2, items.size());
    }
}
