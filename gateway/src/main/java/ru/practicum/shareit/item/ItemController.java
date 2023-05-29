package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final String userHeaderId = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @NotNull @RequestHeader(userHeaderId) Long userId) {
        return itemClient.create(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader(userHeaderId) Long userId,
                                                @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto,
                                         @NotNull @PathVariable Long itemId,
                                         @NotNull @RequestHeader(userHeaderId) Long userId) {
        return itemClient.update(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@NotNull @PathVariable Long itemId,
                                           @NotNull @RequestHeader(userHeaderId) Long userId) {
        return itemClient.findById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@NotNull @RequestHeader(userHeaderId) Long userId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        return itemClient.getAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByRequest(@RequestParam String text,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return itemClient.findByRequest(text, from, size);
    }
}