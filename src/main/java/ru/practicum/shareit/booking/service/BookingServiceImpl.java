package ru.practicum.shareit.booking.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.status.StatusBooking;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingOutDto createBooking(Integer userId, BookingInDto bookingInDto) {
        // Проверка на пересечение бронирований
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                bookingInDto.getItemId(), bookingInDto.getStart(), bookingInDto.getEnd());
        if (!overlappingBookings.isEmpty()) {
            throw new DuplicateDataException("Item is already booked for the requested time period.");
        }
        Booking booking = new Booking();
        booking.setStart(bookingInDto.getStart());
        booking.setEnd(bookingInDto.getEnd());
        booking.setItem(itemRepository.findById(bookingInDto.getItemId())
                .orElseThrow(() -> new NotFoundDataException("Item not found")));
        itemRepository.findById(bookingInDto.getItemId()).filter(Item::getAvailable).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Предмет не доступен"));
        booking.setBooker(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundDataException("User not found")));
        booking.setStatus(StatusBooking.WAITING);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutDto updateBookingStatus(Integer ownerId, Integer bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundDataException("Booking not found"));
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "У предмета другой собственник");
        }
        if (approved) {
            booking.setStatus(StatusBooking.APPROVED);
        } else {
            booking.setStatus(StatusBooking.REJECTED);
        }
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutDto getBooking(Integer userId, Integer bookingId) {
        return bookingRepository.findById(bookingId).filter(b -> b.getBooker().getId().equals(userId) || b.getItem().getOwner().getId().equals(userId))
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new NotFoundDataException("Booking not found"));
    }

    @Override
    public List<BookingOutDto> getUserBookings(Integer userId, String state) {
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now())
                        .stream()
                        .filter(booking -> booking.getStatus() == StatusBooking.APPROVED)
                        .collect(Collectors.toList());
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now())
                        .stream()
                        .filter(booking -> booking.getStatus() == StatusBooking.APPROVED)
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndEndIsAfter(userId, LocalDateTime.now())
                        .stream()
                        .filter(booking -> booking.getStatus() == StatusBooking.APPROVED)
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, StatusBooking.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, StatusBooking.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByBookerId(userId);
        }
        if (bookings.isEmpty()) {
            throw new NotFoundDataException("Booking not found");
        }
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingOutDto> getOwnerBookings(Integer ownerId, String state) {
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBefore(ownerId, LocalDateTime.now())
                        .stream()
                        .filter(booking -> booking.getStatus() == StatusBooking.APPROVED)
                        .collect(Collectors.toList());
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(ownerId, LocalDateTime.now())
                        .stream()
                        .filter(booking -> booking.getStatus() == StatusBooking.APPROVED)
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(ownerId, LocalDateTime.now())
                        .stream()
                        .filter(booking -> booking.getStatus() == StatusBooking.APPROVED)
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, StatusBooking.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, StatusBooking.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerId(ownerId);
        }
        if (bookings.isEmpty()) {
            throw new NotFoundDataException("Booking not found");
        }
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }
}
