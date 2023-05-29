package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto result = new ItemDto();
        result.setId(item.getId());
        result.setName(item.getName());
        result.setDescription(item.getDescription());
        result.setAvailable(item.getAvailable());
        result.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return result;
    }

    public static Item toItemModel(ItemDto itemDto, User owner) {
        Item result = new Item();
        result.setId(itemDto.getId());
        result.setName(itemDto.getName());
        result.setDescription(itemDto.getDescription());
        result.setAvailable(itemDto.getAvailable());
        result.setOwner(owner);
        return result;
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = toItemDto(item);
            result.add(itemDto);
        }
        return result;
    }
}
