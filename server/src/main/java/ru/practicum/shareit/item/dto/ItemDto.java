package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingBriefDto;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingBriefDto lastBooking;
    private BookingBriefDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
