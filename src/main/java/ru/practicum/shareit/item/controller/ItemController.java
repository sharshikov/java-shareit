package ru.practicum.shareit.item.controller;

import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.PostValidationGroup;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    public ItemController(ItemService itemService, CommentService commentService) {
        this.itemService = itemService;
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Validated({PostValidationGroup.class, Default.class}) @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return ResponseEntity.ok(itemService.createItem(itemDto, ownerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok(itemService.getAllItemsByUserId(userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @PathVariable Integer id, @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.updateItem(ownerId, id, itemDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        return ResponseEntity.ok(itemService.searchItems(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto,
                                                    @PathVariable Integer itemId,
                                                    @RequestHeader("X-Sharer-User-Id") Integer authorId) {
        return ResponseEntity.ok(commentService.createComment(commentDto, itemId, authorId));
    }

    @GetMapping("/{itemId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByItemId(@PathVariable Integer itemId) {
        return ResponseEntity.ok(commentService.getCommentsByItemId(itemId));
    }
}
