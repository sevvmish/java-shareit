package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingBriefDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.BookingState;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private User owner;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingBriefDto bookingBriefDto;

    @BeforeEach
    public void beforeEach() {
        user = new User(1L, "Ivanov", "ivanov@mail.ru");
        owner = new User(2L, "Petrov", "petrov@mail.ru");
        item = new Item(1L, "Item", "Description", true, owner, null);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, BookingStatus.APPROVED);
        bookingDto = BookingMapper.toBookingDto(booking);
        bookingBriefDto = BookingMapper.toBookingBriefDto(booking);
    }

    @Test
    @DisplayName("Тест на создание букинга")
    public void createBookingTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto result = bookingService.create(bookingBriefDto, 1L);

        assertEquals(bookingDto.getItem().getId(), result.getItem().getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
    }

    @Test
    @DisplayName("Тест на подтверждение букинга")
    public void approveBookingTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto result = bookingService.approve(1L, 2L, true);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }


    @Test
    @DisplayName("Тест на получение букинга по ИД")
    public void getByIdTest() {
        item.setOwner(owner);

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        BookingDto result = bookingService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Тест на получение автором бронирования букингов в статусе \"отклоненные\"")
    public void findAllByBookerStateRejectedTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerAndStatusEquals(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByUser(1L, BookingState.REJECTED, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение автором бронирования букингов в статусе \"ожидающие\"")
    public void findAllByBookerStateWaitingTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerAndStatusEquals(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByUser(1L, BookingState.WAITING, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение автором бронирования букингов в статусе \"текущие\"")
    public void findAllByBookerStateCurrentTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerAndStartBeforeAndEndAfter(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByUser(1L, BookingState.CURRENT, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение автором бронирования букингов в статусе \"будущие\"")
    public void findAllByBookerStateFutureTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerAndStartAfter(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByUser(1L, BookingState.FUTURE, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение автором бронирования букингов в статусе \"завершенные\"")
    public void findAllByBookerStatePastTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerAndEndBefore(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByUser(1L, BookingState.PAST, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение автором бронирования всех букингов")
    public void findAllByBookerStateAllTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBooker(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService
                .getAllByUser(1L, BookingState.ALL, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    @DisplayName("Тест на получение владельцем букингов в статусе \"ожидающие\"")
    public void findAllByItemOwnerStateWaitingTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwnerAndStatusEquals(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByOwner(1L, BookingState.WAITING, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение владельцем букингов в статусе \"текущие\"")
    public void findAllByItemOwnerStateCurrentTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwnerAndStartBeforeAndEndAfter(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByOwner(1L, BookingState.CURRENT, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение владельцем букингов в статусе \"будущие\"")
    public void findAllByItemOwnerStateFutureTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwnerAndStartAfter(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByOwner(1L, BookingState.FUTURE, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение владельцем букингов в статусе \"завершенные\"")
    public void findAllByItemOwnerStatePastTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwnerAndEndBefore(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByOwner(1L, BookingState.PAST, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение владельцем всех статусов букингов")
    public void findAllByItemOwnerStateAllTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwner(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByOwner(1L, BookingState.ALL, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение владельцем букингов в статусе \"отклоненные\"")
    public void findAllByItemOwnerStateRejectedTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwnerAndStatusEquals(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService
                .getAllByOwner(1L, BookingState.REJECTED, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на ошибку из-за не поддерживаемого статуса букинга")
    public void findAllByBookerUnsupportedStatus() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        BadRequestException e = assertThrows(BadRequestException.class,
                () -> {
                    bookingService.getAllByUser(1L, BookingState.UNSUPPORTED, 0, 10);
                });

        assertNotNull(e);
    }

    @Test
    @DisplayName("Тест на ошибку в датах букинга")
    public void createBookingWrongDateTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        bookingBriefDto.setStart(LocalDateTime.now());
        bookingBriefDto.setEnd(LocalDateTime.now().minusDays(1));

        Exception e = assertThrows(BadRequestException.class,
                () -> {
                    bookingService.create(bookingBriefDto, 1L);
                });
        assertNotNull(e);
    }

    @Test
    @DisplayName("Тест на ошибку в ИД владельца при подтверждении букинга")
    public void approveBookingWrongUserTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> {
                    bookingService.approve(1L, 1L, true);
                });
        assertNotNull(e);
    }

    @Test
    @DisplayName("Тест на ошибку в ИД букинга при подтверждении букинга")
    public void approveBookingWrongBookingIdTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> {
                    bookingService.approve(7L, 1L, true);
                });
        assertNotNull(e);
    }

    @Test
    @DisplayName("Тест на не существующего владельца при создании букинга")
    public void createBookingWrongOwnerTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(null));

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> {
                    bookingService.create(bookingBriefDto, 1L);
                });
        assertNotNull(e);
    }

    @Test
    @DisplayName("Тест на не существующую вещь при создании букинга")
    public void createBookingWrongItemTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(null));

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> {
                    bookingService.create(bookingBriefDto, 1L);
                });
        assertNotNull(e);
    }

    @Test
    @DisplayName("Тест на ошибку подтверждения букинга: не владелец вещи")
    public void approveBookingWrongUserIdTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> {
                    bookingService.approve(1L, 1L, true);
                });
        assertNotNull(e);
    }

}
