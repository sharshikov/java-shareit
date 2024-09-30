package ru.practicum.shareit.item.controller;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.comment.CommentClient;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private final CommentClient commentClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated({PostValidationGroup.class, Default.class})
                                             @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Creating item {}, ownerId={}", itemDto, ownerId);
        return itemClient.createItem(ownerId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                              @PathVariable Integer id) {
        log.info("Getting item with id={}, ownerId={}", id, ownerId);
        return itemClient.getItemById(ownerId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Getting all items for userId={}", userId);
        return itemClient.getAllItemsByUserId(userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                             @PathVariable Integer id,
                                             @RequestBody ItemDto itemDto) {
        log.info("Updating item with id={}, ownerId={}", id, ownerId);
        return itemClient.updateItem(ownerId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                             @PathVariable Integer id) {
        log.info("Deleting item with id={}, ownerId={}", id, ownerId);
        return itemClient.deleteItem(ownerId, id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Searching items with text={}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Integer authorId,
                                                @PathVariable Integer itemId,
                                                @RequestBody CommentDto commentDto) {
        log.info("Creating comment for itemId={}, authorId={}", itemId, authorId);
        return commentClient.createComment(authorId, itemId, commentDto);
    }

    @GetMapping("/{itemId}/comments")
    public ResponseEntity<Object> getCommentsByItemId(@PathVariable Integer itemId) {
        log.info("Getting comments for itemId={}", itemId);
        return commentClient.getCommentsByItemId(itemId);
    }
}
