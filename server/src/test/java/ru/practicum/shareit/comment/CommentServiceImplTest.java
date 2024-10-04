package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.service.CommentService;
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
        booking.setStatus(StatusBooking.APPROVED);
        bookingRepository.save(booking);

        // Создание DTO для тестового комментария
        testCommentDto = new CommentDto();
        testCommentDto.setText("Test comment");
    }

    // Тест на успешное создание комментария
    @Test
    void whenCreateComment_thenCommentIsCreated() {
        CommentDto savedComment = commentService.createComment(testCommentDto, testItem.getId(), testUser.getId());

        assertNotNull(savedComment.getId());
        assertEquals(testCommentDto.getText(), savedComment.getText());
        assertEquals(testUser.getName(), savedComment.getAuthorName());
    }

    // Тест на случай несуществующего предмета
    @Test
    void whenCreateComment_withInvalidItemId_thenThrowsNotFoundDataException() {
        assertThrows(NotFoundDataException.class, () -> {
            commentService.createComment(testCommentDto, 999, testUser.getId());
        });
    }

    // Тест на случай несуществующего пользователя
    @Test
    void whenCreateComment_withInvalidUserId_thenThrowsNotFoundDataException() {
        assertThrows(NotFoundDataException.class, () -> {
            commentService.createComment(testCommentDto, testItem.getId(), 999);
        });
    }

    // Тест на попытку создать комментарий без бронирования
    @Test
    void whenCreateCommentWithoutBooking_thenThrowsResponseStatusException() {
        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("anotheruser@example.com");
        userRepository.save(anotherUser);

        assertThrows(ResponseStatusException.class, () -> {
            commentService.createComment(testCommentDto, testItem.getId(), anotherUser.getId());
        });
    }

    // Тест на получение списка комментариев по предмету
    @Test
    void whenGetCommentsByItemId_thenCommentsAreReturned() {
        Comment comment = new Comment();
        comment.setText("Test comment");
        comment.setItem(testItem);
        comment.setAuthor(testUser);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        List<CommentDto> comments = commentService.getCommentsByItemId(testItem.getId());

        assertEquals(1, comments.size());
        assertEquals(comment.getText(), comments.get(0).getText());
        assertEquals(testUser.getName(), comments.get(0).getAuthorName());
    }

    // Тест на отсутствие комментариев для предмета
    @Test
    void whenGetCommentsByItemId_withNoComments_thenEmptyListReturned() {
        List<CommentDto> comments = commentService.getCommentsByItemId(testItem.getId());
        assertTrue(comments.isEmpty());
    }
}
