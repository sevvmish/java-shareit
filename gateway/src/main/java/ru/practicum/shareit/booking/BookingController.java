package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final String userHeaderId = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingBriefDto bookingBriefDto,
                                         @RequestHeader(userHeaderId) Long userId) {
        return bookingClient.create(bookingBriefDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable Long bookingId,
                                          @RequestHeader(userHeaderId) Long userId,
                                          @RequestParam Boolean approved) {
        return bookingClient.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable Long bookingId, @RequestHeader(userHeaderId) Long userId) {
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(userHeaderId) Long userId,
                                               @RequestParam(defaultValue = "ALL") BookingState state,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingClient.getAllByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(userHeaderId) Long userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingClient.getAllByOwner(state, userId, from, size);
    }
}