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
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);

        testOwner = new User();
        testOwner.setName("Test Owner");
        testOwner.setEmail("testowner@example.com");
        userRepository.save(testOwner);

        testItem = new Item();
        testItem.setName("Test Item");
        testItem.setDescription("Test description");
        testItem.setAvailable(true);
        testItem.setOwner(testOwner);
        itemRepository.save(testItem);

        testBookingInDto = new BookingInDto();
        testBookingInDto.setItemId(testItem.getId());
        testBookingInDto.setStart(LocalDateTime.now().plusDays(1));
        testBookingInDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void whenCreateBooking_thenBookingIsCreated() {
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        assertNotNull(bookingOutDto.getId());
        assertEquals(StatusBooking.WAITING, bookingOutDto.getStatus());
        assertEquals(testItem.getId(), bookingOutDto.getItem().getId());
    }

    @Test
    void whenCreateBooking_withOverlappingDates_thenThrowsDuplicateDataException() {
        bookingService.createBooking(testUser.getId(), testBookingInDto);

        BookingInDto overlappingBooking = new BookingInDto();
        overlappingBooking.setItemId(testItem.getId());
        overlappingBooking.setStart(LocalDateTime.now().plusDays(1));
        overlappingBooking.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(DuplicateDataException.class, () -> bookingService.createBooking(testUser.getId(), overlappingBooking));
    }

    @Test
    void whenCreateBooking_withNonExistentItem_thenThrowsNotFoundDataException() {
        testBookingInDto.setItemId(999);
        assertThrows(NotFoundDataException.class, () -> bookingService.createBooking(testUser.getId(), testBookingInDto));
    }

    @Test
    void whenUpdateBookingStatus_toApproved_thenStatusIsUpdated() {
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        BookingOutDto updatedBooking = bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), true);
        assertEquals(StatusBooking.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void whenUpdateBookingStatus_toRejected_thenStatusIsUpdated() {
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        BookingOutDto updatedBooking = bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), false);
        assertEquals(StatusBooking.REJECTED, updatedBooking.getStatus());
    }

    @Test
    void whenGetBooking_withCorrectUser_thenBookingIsReturned() {
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        BookingOutDto fetchedBooking = bookingService.getBooking(testUser.getId(), bookingOutDto.getId());
        assertEquals(bookingOutDto.getId(), fetchedBooking.getId());
        assertEquals(StatusBooking.WAITING, fetchedBooking.getStatus());
    }

    @Test
    void whenGetBooking_withIncorrectUser_thenThrowsNotFoundDataException() {
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        assertThrows(NotFoundDataException.class, () -> bookingService.getBooking(999, bookingOutDto.getId()));
    }

    @Test
    void whenGetOwnerBookings_withWaitingState_thenBookingsAreReturned() {
        bookingService.createBooking(testUser.getId(), testBookingInDto);
        List<BookingOutDto> bookings = bookingService.getOwnerBookings(testOwner.getId(), "WAITING");
        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void whenGetOwnerBookings_withInvalidState_thenAllBookingsAreReturned() {
        bookingService.createBooking(testUser.getId(), testBookingInDto);
        List<BookingOutDto> bookings = bookingService.getOwnerBookings(testOwner.getId(), "INVALID_STATE");
        assertFalse(bookings.isEmpty());
    }

    // Новый тест для состояния PAST
    @Test
    void whenGetOwnerBookings_withPastState_thenBookingsAreReturned() {
        testBookingInDto.setStart(LocalDateTime.now().minusDays(5));
        testBookingInDto.setEnd(LocalDateTime.now().minusDays(3));
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), true);
        List<BookingOutDto> bookings = bookingService.getOwnerBookings(testOwner.getId(), "PAST");
        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.APPROVED, bookings.get(0).getStatus());
    }

    // Новый тест для состояния PAST
    @Test
    void whenGetOwnerBookings_withCurrentState_thenBookingsAreReturned() {
        testBookingInDto.setStart(LocalDateTime.now().minusDays(5));
        testBookingInDto.setEnd(LocalDateTime.now().plusDays(3));
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), true);
        List<BookingOutDto> bookings = bookingService.getOwnerBookings(testOwner.getId(), "CURRENT");
        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.APPROVED, bookings.get(0).getStatus());
    }

    // Новый тест для состояния FUTURE
    @Test
    void whenGetOwnerBookings_withFutureState_thenBookingsAreReturned() {
        testBookingInDto.setStart(LocalDateTime.now().plusDays(5));
        testBookingInDto.setEnd(LocalDateTime.now().plusDays(7));
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), true);
        List<BookingOutDto> bookings = bookingService.getOwnerBookings(testOwner.getId(), "FUTURE");
        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.APPROVED, bookings.get(0).getStatus());
    }

    // Новый тест для состояния REJECTED
    @Test
    void whenGetOwnerBookings_withRejectedState_thenBookingsAreReturned() {
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), false);
        List<BookingOutDto> bookings = bookingService.getOwnerBookings(testOwner.getId(), "REJECTED");
        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.REJECTED, bookings.get(0).getStatus());
    }

    // Новый тест для фильтрации REJECTED
    @Test
    void whenGetOwnerBookings_withRejectedStatus_thenFilteredOut() {
        testBookingInDto.setStart(LocalDateTime.now().plusDays(1));
        testBookingInDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), false); // Set to REJECTED
        assertThrows(NotFoundDataException.class, () -> bookingService.getOwnerBookings(testOwner.getId(), "FUTURE"));
    }

    // Тесты для метода getUserBookings

    @Test
    void whenGetUserBookings_withFutureState_thenBookingsAreReturned() {
        testBookingInDto.setStart(LocalDateTime.now().plusDays(3));
        testBookingInDto.setEnd(LocalDateTime.now().plusDays(4));
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), true);
        List<BookingOutDto> bookings = bookingService.getUserBookings(testUser.getId(), "FUTURE");
        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void whenGetUserBookings_withCurrentState_thenBookingsAreReturned() {
        testBookingInDto.setStart(LocalDateTime.now().minusDays(1));
        testBookingInDto.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), true);
        List<BookingOutDto> bookings = bookingService.getUserBookings(testUser.getId(), "CURRENT");
        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void whenGetUserBookings_withPastState_thenBookingsAreReturned() {
        testBookingInDto.setStart(LocalDateTime.now().minusDays(4));
        testBookingInDto.setEnd(LocalDateTime.now().minusDays(2));
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), true);
        List<BookingOutDto> bookings = bookingService.getUserBookings(testUser.getId(), "PAST");
        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void whenGetUserBookings_withRejectedState_thenBookingsAreReturned() {
        BookingOutDto bookingOutDto = bookingService.createBooking(testUser.getId(), testBookingInDto);
        bookingService.updateBookingStatus(testOwner.getId(), bookingOutDto.getId(), false);
        List<BookingOutDto> bookings = bookingService.getUserBookings(testUser.getId(), "REJECTED");
        assertFalse(bookings.isEmpty());
        assertEquals(StatusBooking.REJECTED, bookings.get(0).getStatus());
    }
}
