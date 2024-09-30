package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto, Integer itemId, Integer authorId);

    List<CommentDto> getCommentsByItemId(Integer itemId);
}
