package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.status.StatusBooking;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        // Настройка объектов для тестов
        BookingOutDto lastBooking = new BookingOutDto(1, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, null, StatusBooking.APPROVED);
        BookingOutDto nextBooking = new BookingOutDto(2, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), null, null, StatusBooking.APPROVED);

        commentDto = new CommentDto(1, "Comment Text", "Author", LocalDateTime.now());

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(Collections.singletonList(commentDto));
    }

    @Test
    void whenCreateItem_thenReturnItem() throws Exception {
        when(itemService.createItem(any(ItemDto.class), anyInt())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void whenGetItemById_thenReturnItem() throws Exception {
        when(itemService.getItemById(anyInt())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void whenGetAllItemsByUserId_thenReturnItems() throws Exception {
        when(itemService.getAllItemsByUserId(anyInt())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void whenUpdateItem_thenReturnUpdatedItem() throws Exception {
        when(itemService.updateItem(anyInt(), anyInt(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void whenDeleteItem_thenNoContent() throws Exception {
        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenSearchItems_thenReturnItemList() throws Exception {
        when(itemService.searchItems(any(String.class))).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search?text=test"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void whenCreateComment_thenReturnComment() throws Exception {
        when(commentService.createComment(any(CommentDto.class), anyInt(), anyInt())).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }

    @Test
    void whenGetCommentsByItemId_thenReturnComments() throws Exception {
        when(commentService.getCommentsByItemId(anyInt())).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/items/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(commentDto))));
    }
}
