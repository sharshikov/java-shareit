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
    private final ItemMapper itemMapper = ItemMapper.INSTANCE;
    private final CommentMapper commentMapper = CommentMapper.INSTANCE;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
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

    @Override
    public List<ItemDto> getAllItemsByUserId(Integer userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Integer ownerId, Integer id, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));
        if (!existingItem.getOwner().getId().equals(ownerId))
            throw new NotFoundDataException("Предмет не найден");
        if (itemDto.getName() != null)
            existingItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            existingItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            existingItem.setAvailable(itemDto.getAvailable());
        return itemMapper.toDto(itemRepository.save(existingItem));
    }

    @Override
    public void deleteItem(Integer id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream().filter(Item::getAvailable)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}
