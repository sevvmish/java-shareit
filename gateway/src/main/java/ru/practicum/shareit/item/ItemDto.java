package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.booking.BookingBriefDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ItemDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private BookingBriefDto lastBooking;

    private BookingBriefDto nextBooking;

    private List<CommentDto> comments;

    private Long requestId;
}