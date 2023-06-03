package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequest itemRequest;
    private User user;

    @BeforeEach
    private void beforeEach() {

        user = new User(1L, "Ivanov", "ivanov@mail.ru");
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.parse("2023-10-01T19:34:50.63"));
    }

    @Test
    @DisplayName("Тест на создание реквеста")
    public void createTest() {
        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setId(1L);
        inputDto.setDescription(itemRequest.getDescription());

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto responseDto = itemRequestService.create(1L, inputDto);

        assertEquals(1L, responseDto.getId());
        assertEquals(inputDto.getDescription(), responseDto.getDescription());
    }

    @Test
    @DisplayName("Тест на получение всех реквестов по ИД юзера")
    void getAllByUserTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository
                .findAllByRequestorIdOrderByCreatedAsc(any(Long.class)))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> result = itemRequestService.getAllByUser(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение всех реквестов")
    void getAllTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.findAllByRequestorIsNot(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<ItemRequestDto> result = itemRequestService.getAll(0, 10, 1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение реквеста по ИД")
    void getByIdTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(itemRequest));

        when(itemRepository.findAllByRequestId(any(Long.class)))
                .thenReturn(new ArrayList<>());


        ItemRequestDto result = itemRequestService.getById(1L, 1L);

        assertEquals(1L, result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }
}
