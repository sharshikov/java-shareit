package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.status.StatusBooking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
public class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User booker;
    private User owner;
    private Item testItem;
    private BookingInDto testBookingInDto;

    @BeforeEach
    void setup() {
        // Создание тестовых пользователей
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        userRepository.save(booker);

        // Создание тестового предмета
        testItem = new Item();
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setAvailable(true);
        testItem.setOwner(owner);
        itemRepository.save(testItem);

        // Создание DTO для тестового бронирования
        testBookingInDto = new BookingInDto();
        testBookingInDto.setItemId(testItem.getId());
        testBookingInDto.setStart(LocalDateTime.now().plusDays(1));
        testBookingInDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void whenCreateBooking_thenBookingIsCreated() {
        // Создание бронирования через сервис
        var savedBooking = bookingService.createBooking(booker.getId(), testBookingInDto);

        // Проверка, что бронирование было успешно создано
        assertNotNull(savedBooking.getId());
        assertEquals(testBookingInDto.getStart(), savedBooking.getStart());
        assertEquals(testBookingInDto.getEnd(), savedBooking.getEnd());
        assertEquals(StatusBooking.WAITING, savedBooking.getStatus());
    }

    @Test
    void whenGetBookingById_thenBookingIsReturned() {
        // Создание бронирования через репозиторий
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(testItem);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(StatusBooking.WAITING);
        bookingRepository.save(booking);

        // Получение бронирования через сервис
        var foundBooking = bookingService.getBooking(booker.getId(), booking.getId());

        // Проверка, что возвращенное бронирование соответствует сохраненному
        assertNotNull(foundBooking);
        assertEquals(booking.getId(), foundBooking.getId());
        assertEquals(booking.getStart(), foundBooking.getStart());
        assertEquals(booking.getEnd(), foundBooking.getEnd());
        assertEquals(booking.getStatus(), foundBooking.getStatus());
    }

    @Test
    void whenUpdateBookingStatus_thenBookingIsUpdated() {
        // Создание и сохранение бронирования
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(testItem);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(StatusBooking.WAITING);
        bookingRepository.save(booking);

        // Обновление статуса бронирования через сервис
        var updatedBooking = bookingService.updateBookingStatus(owner.getId(), booking.getId(), true);

        // Проверка, что статус был обновлен
        assertNotNull(updatedBooking);
        assertEquals(StatusBooking.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void whenDeleteBooking_thenBookingIsDeleted() {
        // Создание и сохранение бронирования
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(testItem);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(StatusBooking.WAITING);
        bookingRepository.save(booking);

        // Удаление бронирования через репозиторий
        bookingRepository.delete(booking);

        // Проверка, что бронирование было удалено
        Optional<Booking> deletedBooking = bookingRepository.findById(booking.getId());
        assertFalse(deletedBooking.isPresent());
    }

    @Test
    void whenGetAllBookingsByBooker_thenListOfBookingsIsReturned() {
        // Создание и сохранение бронирований
        Booking booking1 = new Booking();
        booking1.setBooker(booker);
        booking1.setItem(testItem);
        booking1.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking1.setStatus(StatusBooking.WAITING);

        Booking booking2 = new Booking();
        booking2.setBooker(booker);
        booking2.setItem(testItem);
        booking2.setStart(LocalDateTime.now().plusDays(3));
        booking2.setEnd(LocalDateTime.now().plusDays(4));
        booking2.setStatus(StatusBooking.WAITING);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        // Получение всех бронирований пользователя через сервис
        var bookings = bookingService.getUserBookings(booker.getId(), "ALL");

        // Проверка, что список содержит созданные бронирования
        assertEquals(2, bookings.size());
    }
}
