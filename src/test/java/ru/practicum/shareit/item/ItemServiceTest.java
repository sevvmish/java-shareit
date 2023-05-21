package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.WrongUserException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private User user;
    private ItemDto itemDto;
    private Comment comment;
    private Booking booking;
    private CommentDto commentDto;

    private ItemRequest itemRequest;

    @BeforeEach
    public void beforeEach() {
        user = new User(1L, "Ivanov", "ivanov@mail.ru");
        itemRequest = new ItemRequest(1L, "description", new User(2L, "Petrov", "Petrov@mail.ru"), LocalDateTime.now());
        item = new Item(1L, "Item", "Description", true, user, itemRequest);
        itemDto = ItemMapper.toItemDto(item);
        comment = new Comment(1L, "comment", item, user, LocalDateTime.now());
        commentDto = CommentMapper.toCommentDto(comment);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, BookingStatus.APPROVED);
    }

    @Test
    @DisplayName("Тест на создание предмета")
    public void createItemTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        when(itemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(itemRequest));

        ItemDto result = itemService.create(itemDto, 1L);

        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getRequestId(), result.getRequestId());
    }

    @Test
    @DisplayName("Тест на ошибку при создании предмета: нет такого пользователя")
    public void createItemNoUserTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(null));

        ObjectNotFoundException result = assertThrows(ObjectNotFoundException.class, () -> {
            itemService.createComment(1L, 1L, commentDto);
        });

        assertNotNull(result);
    }

    @Test
    @DisplayName("Тест на создание комментария")
    public void createCommentTest() {
        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(any(Long.class), any(Long.class), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto result = itemService.createComment(1L, 1L, commentDto);

        assertEquals(commentDto.getText(), result.getText());
        assertEquals(user.getName(), result.getAuthorName());
    }

    @Test
    @DisplayName("Тест на обновление предмета")
    public void updateItemTest() {
        itemDto.setName("updatedName");
        item.setName("updatedName");

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        ItemDto result = itemService.update(itemDto, itemDto.getId(), user.getId());

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
    }

    @Test
    @DisplayName("Тест на ошибку в ИД владельца при обновление предмета")
    public void updateItemWrongOwnerTest() {
        itemDto.setName("updatedName");
        item.setName("updatedName");

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(WrongUserException.class,
                () -> {
                    itemService.update(itemDto, itemDto.getId(), 7L);
                });
        assertNotNull(e);
    }

    @Test
    @DisplayName("Тест на поиск предмета по ИД")
    public void findItemByIdTest() {
        booking.setStart(LocalDateTime.now());
        booking.setEnd(null);

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        when(commentRepository.findAllByItemId(any(Long.class)))
                .thenReturn(new ArrayList<>());

        ItemDto result = itemService.findById(1L, 1L);

        assertEquals(1L, result.getId());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    @DisplayName("Тест на ошибку при поиске предмета по ИД")
    public void findItemByIdNoItemTest() {
        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(null));

        ObjectNotFoundException result = assertThrows(ObjectNotFoundException.class, () -> {
            itemService.findById(1L, 1L);
        });

        assertNotNull(result);
    }

    @Test
    @DisplayName("Тест на получение всех предметов владельцем")
    public void findAllItemsTest() {
        when(itemRepository.findAllByOwnerId(any(Long.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<ItemDto> result = itemService.getAllByUserId(1L, 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение предметов по поисковому запросу")
    public void findItemsByRequestTest() {
        when(itemRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        List<Item> result = itemService.findByRequest("Item", 0, 10);

        assertNotNull(result);
        assertEquals(result.get(0).getId(), item.getId());
        assertEquals(result.get(0).getName(), item.getName());
    }

    @Test
    @DisplayName("Тест на ошибку в создании комментария")
    public void createCommentExceptionTest() {
        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore
                        (any(Long.class), any(Long.class), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        BadRequestException result = assertThrows(BadRequestException.class, () -> {
            itemService.createComment(1L, 1L, commentDto);
        });

        assertNotNull(result);
    }
}
