package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.status.StatusBooking;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingOutDto bookingOutDto;
    private BookingInDto bookingInDto;

    @BeforeEach
    void setup() {
        // Создание тестовых данных для DTO
        bookingInDto = new BookingInDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Test Item");

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("Test User");

        bookingOutDto = new BookingOutDto(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), userDto, itemDto, StatusBooking.APPROVED);
    }

    @Test
    void whenCreateBooking_thenReturnBookingOutDto() throws Exception {
        when(bookingService.createBooking(anyInt(), any(BookingInDto.class))).thenReturn(bookingOutDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingOutDto.getId()))
                .andExpect(jsonPath("$.item.id").value(bookingOutDto.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingOutDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingOutDto.getStatus().toString()));
    }

    @Test
    void whenUpdateBookingStatus_thenReturnUpdatedBooking() throws Exception {
        bookingOutDto.setStatus(StatusBooking.REJECTED);
        when(bookingService.updateBookingStatus(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingOutDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingOutDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingOutDto.getStatus().toString()));
    }

    @Test
    void whenUpdateBookingStatusWithInvalidApprovedParam_thenReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "invalid")) // Некорректный параметр
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetBookingById_thenReturnBookingOutDto() throws Exception {
        when(bookingService.getBooking(anyInt(), anyInt())).thenReturn(bookingOutDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingOutDto.getId()))
                .andExpect(jsonPath("$.item.id").value(bookingOutDto.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingOutDto.getBooker().getId()));
    }

    @Test
    void whenGetBookingByInvalidId_thenReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", "invalidId") // Некорректный ID
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetUserBookings_thenReturnListOfBookings() throws Exception {
        when(bookingService.getUserBookings(anyInt(), anyString())).thenReturn(List.of(bookingOutDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingOutDto.getId()))
                .andExpect(jsonPath("$[0].item.id").value(bookingOutDto.getItem().getId()));
    }

    @Test
    void whenGetOwnerBookings_thenReturnListOfBookings() throws Exception {
        when(bookingService.getOwnerBookings(anyInt(), anyString())).thenReturn(List.of(bookingOutDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingOutDto.getId()))
                .andExpect(jsonPath("$[0].item.id").value(bookingOutDto.getItem().getId()));
    }
}
