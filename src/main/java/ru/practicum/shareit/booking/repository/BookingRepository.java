package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.status.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdAndEndIsBefore(Integer bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndEndIsAfter(Integer bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStatus(Integer bookerId, StatusBooking status);

    List<Booking> findByBookerId(Integer bookerId);

    List<Booking> findByItemOwnerIdAndStartIsBefore(Integer ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Integer ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Integer ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStatus(Integer ownerId, StatusBooking status);

    List<Booking> findByItemOwnerId(Integer ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND (b.start < ?3 AND b.end > ?2)")
    List<Booking> findOverlappingBookings(Integer itemId, LocalDateTime start, LocalDateTime end);
}
