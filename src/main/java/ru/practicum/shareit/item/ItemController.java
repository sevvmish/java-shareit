package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemMapper mapper;
    private final ItemService itemService;
    public static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @NotNull @Min(1) @RequestHeader(USER_ID) Long userId) {
        Item item = mapper.toItemModel(itemDto, userId);
        return mapper.toItemDto(itemService.create(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @NotNull @Min(1) @PathVariable Long itemId,
                          @NotNull @Min(1) @RequestHeader(USER_ID) Long userId) {
        Item item = mapper.toItemModel(itemDto, userId);
        item.setId(itemId);
        return mapper.toItemDto(itemService.update(item));
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@NotNull @Min(1) @PathVariable Long itemId) {
        return mapper.toItemDto(itemService.findById(itemId));
    }

    @GetMapping
    public List<ItemDto> getAllByUserId(@NotNull @Min(1) @RequestHeader(USER_ID) Long userId) {
        List<Item> userItems = itemService.getAllByUserId(userId);
        return mapper.toItemDtoList(userItems);
    }

    @GetMapping("/search")
    public List<ItemDto> findByRequest(@RequestParam String text) {
        List<Item> foundItems = itemService.findByRequest(text);
        return mapper.toItemDtoList(foundItems);
    }
}
