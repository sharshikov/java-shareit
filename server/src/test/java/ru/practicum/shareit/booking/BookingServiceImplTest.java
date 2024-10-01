package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.DuplicateDataException;
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
class BookingServiceImplTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User testOwner;
    private Item testItem;
    private BookingInDto testBookingInDto;

    @BeforeEach
    void setup() {
        // Создание тестового пользователя
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);

        // Создание владельца предмета
        testOwner = new User();
        testOwner.setName("Test Owner");
        testOwner.setEmail("testowner@example.com");
        userRepository.save(testOwner);

        // Создание тестового предмета
        testItem = new Item();
        testItem.setName("Test Item");
        testItem.setDescription("Test description");
        testItem.setAvailable(true);
        testItem.setOwner(testOwner);
        itemRepository.save(testItem);

        // Создание тестового BookingInDto
        testBookingInDto = new BookingInDto();
        testBookingInDto.setItemId(testItem.getId());
        testBookingInDto.setStart(LocalDateTime.now().plusDays(1));
        testBookingInDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void whenCreateBooking_thenBookingIsCreated() {
        // Создание бронирования
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);

        // Проверка, что бронирование создано
        assertNotNull(bookingOutDto.getId());
        assertEquals(StatusBooking.WAITING, bookingOutDto.getStatus());
        assertEquals(testItem.getId(), bookingOutDto.getItem().getId());
    }

    @Test
    void whenCreateBooking_withOverlappingDates_thenThrowsDuplicateDataException() {
        // Создание первого бронирования
        bookingService.createBooking(testUser.getId(), testBookingInDto);

        // Создание второго бронирования с пересекающимися датами
        BookingInDto overlappingBooking = new BookingInDto();
        overlappingBooking.setItemId(testItem.getId());
        overlappingBooking.setStart(LocalDateTime.now().plusDays(1));
        overlappingBooking.setEnd(LocalDateTime.now().plusDays(2));

        // Ожидание исключения
        assertThrows(DuplicateDataException.class, () -> bookingService.createBooking(testUser.getId(), overlappingBooking));
    }

    @Test
    void whenCreateBooking_withNonExistentItem_thenThrowsNotFoundDataException() {
        testBookingInDto.setItemId(999);

        // Ожидание исключения
        assertThrows(NotFoundDataException.class, () -> bookingService.createBooking(testUser.getId(), testBookingInDto));
    }

    @Test
    void whenUpdateBookingStatus_toApproved_thenStatusIsUpdated() {
        // Создание бронирования
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);

        // Обновление статуса бронирования владельцем предмета
        BookingOutDto updatedBooking = bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), true);

        // Проверка, что статус обновлен на APPROVED
        assertEquals(StatusBooking.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void whenUpdateBookingStatus_toRejected_thenStatusIsUpdated() {
        // Создание бронирования
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);

        // Обновление статуса бронирования владельцем предмета на REJECTED
        BookingOutDto updatedBooking = bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), false);

        // Проверка, что статус обновлен на REJECTED
        assertEquals(StatusBooking.REJECTED, updatedBooking.getStatus());
    }

    @Test
    void whenUpdateBookingStatus_withWrongOwner_thenThrowsResponseStatusException() {
        // Создание бронирования
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);

        // Ожидание исключения при попытке обновить статус от имени другого владельца
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> {
            bookingService.updateBookingStatus(testUser.getId(), bookingOutDto.getId(), true);
        });
    }

    @Test
    void whenGetBooking_withCorrectUser_thenBookingIsReturned() {
        // Создание бронирования
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);

        // Получение бронирования через сервис
        BookingOutDto fetchedBooking = bookingService.getBooking(testUser.getId(), bookingOutDto.getId());

        // Проверка, что бронирование возвращено
        assertEquals(bookingOutDto.getId(), fetchedBooking.getId());
        assertEquals(StatusBooking.WAITING, fetchedBooking.getStatus());
    }

    @Test
    void whenGetBooking_withIncorrectUser_thenThrowsNotFoundDataException() {
        // Создание бронирования
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);

        // Ожидание исключения при попытке получить бронирование другим пользователем
        assertThrows(NotFoundDataException.class, () -> bookingService.getBooking(999, bookingOutDto.getId()));
    }

    @Test
    void whenGetOwnerBookings_withWaitingState_thenBookingsAreReturned() {
        // Создание бронирования
        bookingService.createBooking(testUser.getId(), testBookingInDto);

        // Получение бронирований владельца в состоянии WAITING
        List<BookingOutDto> bookings = bookingService.getOwnerBookings(testOwner.getId(), "WAITING");

        // Проверка, что бронирования были успешно возвращены
        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void whenGetOwnerBookings_withInvalidState_thenAllBookingsAreReturned() {
        // Создание бронирования
        bookingService.createBooking(testUser.getId(), testBookingInDto);

        // Получение всех бронирований владельца
        List<BookingOutDto> bookings = bookingService.getOwnerBookings(testOwner.getId(), "INVALID_STATE");

        // Проверка, что возвращены все бронирования
        assertFalse(bookings.isEmpty());
    }

    @Test
    void whenGetUserBookings_withFutureState_thenBookingsAreReturned() {
        // Изменение времени начала бронирования на несколько дней вперед, чтобы оно точно было в будущем
        testBookingInDto.setStart(LocalDateTime.now().plusDays(3));
        testBookingInDto.setEnd(LocalDateTime.now().plusDays(4));

        // Создаем бронирование
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), true);
        // Проверка бронирований пользователя в будущем состоянии
        List<BookingOutDto> bookings = bookingService.getUserBookings(testUser.getId(), "FUTURE");

        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void whenGetUserBookings_withCurrentState_thenBookingsAreReturned() {
        // Устанавливаем даты для текущего периода
        testBookingInDto.setStart(LocalDateTime.now().minusDays(1));
        testBookingInDto.setEnd(LocalDateTime.now().plusDays(1));

        // Создаем бронирование
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), true);

        // Проверка бронирований пользователя в состоянии CURRENT
        List<BookingOutDto> bookings = bookingService.getUserBookings(testUser.getId(), "CURRENT");

        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void whenGetUserBookings_withPastState_thenBookingsAreReturned() {
        // Устанавливаем даты для прошлого периода
        testBookingInDto.setStart(LocalDateTime.now().minusDays(4));
        testBookingInDto.setEnd(LocalDateTime.now().minusDays(2));

        // Создаем бронирование
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), true);

        // Проверка бронирований пользователя в состоянии PAST
        List<BookingOutDto> bookings = bookingService.getUserBookings(testUser.getId(), "PAST");

        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.APPROVED, bookings.get(0).getStatus());
    }
}
