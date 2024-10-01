package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class ItemServiceImplTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemMapper itemMapper;

    private User testUser;
    private ItemDto testItemDto;

    @BeforeEach
    void setUp() {
        // Создаем тестового пользователя
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);

        // Создаем тестовый предмет
        testItemDto = new ItemDto();
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test description");
        testItemDto.setAvailable(true);
    }

    @Test
    void whenCreateItem_thenItemIsCreated() {
        // Создаем предмет через сервис
        ItemDto createdItem = itemService.createItem(testItemDto, testUser.getId());

        // Проверяем, что предмет был создан и все данные корректны
        assertNotNull(createdItem.getId());
        assertEquals(testItemDto.getName(), createdItem.getName());
        assertEquals(testUser.getId(), createdItem.getOwnerId());
    }

    @Test
    void whenCreateItem_withNonExistingUser_thenThrowNotFoundDataException() {
        // Ожидаем исключение при создании предмета для несуществующего пользователя
        assertThrows(NotFoundDataException.class, () -> {
            itemService.createItem(testItemDto, 999);
        });
    }

    @Test
    void whenGetItemById_thenReturnItem() {
        // Создаем и сохраняем предмет в репозитории
        Item createdItem = itemMapper.toEntity(testItemDto);
        createdItem.setOwner(testUser);
        itemRepository.save(createdItem);

        // Получаем предмет по id через сервис
        ItemDto foundItem = itemService.getItemById(createdItem.getId());

        // Проверяем, что предмет был успешно найден и данные корректны
        assertNotNull(foundItem);
        assertEquals(testItemDto.getName(), foundItem.getName());
    }

    @Test
    void whenGetItemById_withNonExistingItem_thenThrowException() {
        // Ожидаем исключение при попытке получить несуществующий предмет
        assertThrows(RuntimeException.class, () -> itemService.getItemById(999));
    }

    @Test
    void whenGetItemWithComments_thenReturnItemWithComments() {
        // Создаем и сохраняем предмет
        Item createdItem = itemMapper.toEntity(testItemDto);
        createdItem.setOwner(testUser);
        itemRepository.save(createdItem);

        // Создаем и сохраняем комментарий
        Comment comment = new Comment();
        comment.setText("Test comment");
        comment.setItem(createdItem);
        comment.setAuthor(testUser);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        // Получаем предмет с комментариями через сервис
        ItemDto foundItem = itemService.getItemById(createdItem.getId());

        // Проверяем наличие комментариев
        assertNotNull(foundItem.getComments());
        assertEquals(1, foundItem.getComments().size());
        assertEquals(comment.getText(), foundItem.getComments().get(0).getText());
    }
}
