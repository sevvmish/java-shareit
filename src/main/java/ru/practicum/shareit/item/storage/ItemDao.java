package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item create(Item item);

    Item update(Item item);

    Item findById(Long itemId);

    List<Item> getAllByUserId(Long userId);

    List<Item> findByRequest(String request);
}
