package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String userHeaderId = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(userHeaderId) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(userHeaderId) Long userId) {
        return itemRequestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestHeader(userHeaderId) Long userId) {
        return itemRequestService.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Long requestId, @RequestHeader(userHeaderId) Long userId) {
        return itemRequestService.getById(requestId, userId);
    }
}
