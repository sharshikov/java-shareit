package ru.practicum.shareit.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.status.StatusBooking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
public class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private CommentDto testCommentDto;
    private Item testItem;
    private User testUser;

    @BeforeEach
    void setup() {
        // Создание тестового пользователя
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);

        // Создание тестового предмета
        testItem = new Item();
        testItem.setName("Test Item");
        testItem.setDescription("Test description");
        testItem.setAvailable(true);
        testItem.setOwner(testUser);
        itemRepository.save(testItem);

        // Создание тестового бронирования
        Booking booking = new Booking();
        booking.setItem(testItem);
        booking.setBooker(testUser);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(StatusBooking.APPROVED);  // Установите статус бронирования
        bookingRepository.save(booking);

        // Создание DTO для тестового комментария
        testCommentDto = new CommentDto();
        testCommentDto.setText("Test comment");
    }

    @Test
    void whenCreateComment_thenCommentIsCreated() {
        // Создание комментария через сервис
        CommentDto savedComment = commentService.createComment(testCommentDto, testItem.getId(), testUser.getId());

        // Проверка, что комментарий был успешно создан
        assertNotNull(savedComment.getId());
        assertEquals(testCommentDto.getText(), savedComment.getText());
        assertEquals(testUser.getName(), savedComment.getAuthorName());
    }

    @Test
    void whenCreateComment_withInvalidItemId_thenThrowsNotFoundDataException() {
        // Проверка, что возникает исключение при неверном ID предмета
        assertThrows(NotFoundDataException.class, () -> {
            commentService.createComment(testCommentDto, 999, testUser.getId());
        });
    }

    @Test
    void whenCreateComment_withInvalidUserId_thenThrowsNotFoundDataException() {
        // Проверка, что возникает исключение при неверном ID пользователя
        assertThrows(NotFoundDataException.class, () -> {
            commentService.createComment(testCommentDto, testItem.getId(), 999);
        });
    }

    @Test
    void whenGetCommentsByItemId_thenCommentsAreReturned() {
        // Создание и сохранение комментария
        Comment comment = new Comment();
        comment.setText("Test comment");
        comment.setItem(testItem);
        comment.setAuthor(testUser);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        // Получение комментариев через сервис
        List<CommentDto> comments = commentService.getCommentsByItemId(testItem.getId());

        // Проверка, что список содержит созданные комментарии
        assertEquals(1, comments.size());
        assertEquals(comment.getText(), comments.get(0).getText());
        assertEquals(testUser.getName(), comments.get(0).getAuthorName());
    }
}
