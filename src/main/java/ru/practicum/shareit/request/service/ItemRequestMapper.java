package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest result = new ItemRequest();
        result.setId(itemRequestDto.getId());
        result.setDescription(itemRequestDto.getDescription());
        return result;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto result = new ItemRequestDto();
        result.setId(itemRequest.getId());
        result.setDescription(itemRequest.getDescription());
        result.setCreated(itemRequest.getCreated());
        result.setItems(new ArrayList<>());
        return result;
    }
}
