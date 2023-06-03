package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {

    private ItemRequest itemRequest;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    private void beforeEach() {
        itemRequest = new ItemRequest(1L, "Description", null, null);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Description");
    }

    @Test
    @DisplayName("Тест на перевод запроса в специальный объект запроса")
    public void toItemRequestDtoTest() {
        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(dto.getId(), itemRequest.getId());
        assertEquals(dto.getDescription(), itemRequest.getDescription());
    }

    @Test
    @DisplayName("Тест на перевод специального объекта запроса в запрос")
    public void toItemRequestTest() {
        ItemRequest newItemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);

        assertEquals(newItemRequest.getId(), itemRequestDto.getId());
        assertEquals(newItemRequest.getDescription(), itemRequestDto.getDescription());
    }
}
