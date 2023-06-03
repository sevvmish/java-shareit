package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestDto {
    private Long id;

    @NotBlank
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}