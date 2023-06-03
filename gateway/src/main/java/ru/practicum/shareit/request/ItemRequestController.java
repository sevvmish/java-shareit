package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final String userHeaderId = "X-Sharer-User-Id";

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(userHeaderId) Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return requestClient.create(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(userHeaderId) Long userId) {
        return requestClient.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size,
                                         @RequestHeader(userHeaderId) Long userId) {
        return requestClient.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Long requestId, @RequestHeader(userHeaderId) Long userId) {
        return requestClient.getById(requestId, userId);
    }
}
