package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingBriefDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String userHeaderId = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingBriefDto bookingBriefDto,
                             @RequestHeader(userHeaderId) Long userId) {
        return bookingService.create(bookingBriefDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId, @RequestHeader(userHeaderId) Long userId,
                              @RequestParam Boolean approved) {
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(userHeaderId) Long userId,
                                          @RequestParam(defaultValue = "ALL") BookingState state,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingService.getAllByOwner(userId, state, from, size);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader(userHeaderId) Long userId,
                                         @RequestParam(defaultValue = "ALL") BookingState state,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingService.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId, @RequestHeader(userHeaderId) Long userId) {
        return bookingService.getById(bookingId, userId);
    }
}