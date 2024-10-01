package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, CommentRepository commentRepository, ItemMapper itemMapper, CommentMapper commentMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer ownerId) {
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundDataException("Пользователь не найден")));
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Integer id) {
        ItemDto itemDto = itemRepository.findById(id)
                .map(itemMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));
        itemDto.setComments(commentRepository.findByItemId(id).stream().map(commentMapper::toDto).toList());
        return itemDto;
    }
}
