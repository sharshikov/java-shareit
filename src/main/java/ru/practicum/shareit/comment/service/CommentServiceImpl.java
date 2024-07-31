package ru.practicum.shareit.comment.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper = CommentMapper.INSTANCE;

    public CommentServiceImpl(CommentRepository commentRepository, ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.commentRepository = commentRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Integer itemId, Integer authorId) {
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setItem(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundDataException("Item not found")));
        comment.setAuthor(userRepository.findById(authorId).orElseThrow(() -> new NotFoundDataException("User not found")));
        bookingRepository.findByBookerId(authorId).stream()
                .filter(x -> x.getItem().getId().equals(itemId) && LocalDateTime.now().isAfter(x.getStart()))
                .findAny().orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Integer itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }
}
