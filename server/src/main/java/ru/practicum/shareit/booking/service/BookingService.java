package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

public interface BookingService {
    BookingOutDto createBooking(Integer userId, BookingInDto bookingInDto);

    BookingOutDto updateBookingStatus(Integer userId, Integer bookingId, Boolean approved);

    BookingOutDto getBooking(Integer userId, Integer bookingId);

    List<BookingOutDto> getUserBookings(Integer userId, String state);

    List<BookingOutDto> getOwnerBookings(Integer userId, String state);
}
