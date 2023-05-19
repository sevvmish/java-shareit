package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingBriefDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingBriefDto bookingBriefDto, Long userId);

    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    List<BookingDto> getAllByOwner(Long userId, BookingState state, int from, int size);

    List<BookingDto> getAllByUser(Long userId, BookingState state, int from, int size);

    BookingDto getById(Long itemId, Long userId);

}

