package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public Item toItemModel(ItemDto itemDto, Long ownerId) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), ownerId);
    }

    public List<ItemDto> toItemDtoList(List<Item> items) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = toItemDto(item);
            result.add(itemDto);
        }
        return result;
    }
}
