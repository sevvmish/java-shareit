package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto item, Long userId);

    ItemDto update(ItemDto item, Long itemId, Long userId);

    ItemDto findById(Long itemId, Long userId);

    List<ItemDto> getAllByUserId(Long userId, int from, int size);

    List<Item> findByRequest(String request, int from, int size);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
