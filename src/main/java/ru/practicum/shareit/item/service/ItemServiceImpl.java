package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingBriefDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.WrongUserException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));

        Item item = ItemMapper.toItemModel(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item with id:" + itemId + " not found error"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new WrongUserException("wrong user:" + item.getOwner().getId() + " is not an owner of " + itemDto);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto findById(Long itemId, Long userId) {
        /*
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item with id:" + itemId + " not found error"));
        ItemDto itemDto = ItemMapper.toItemDto(item);

        itemDto.setComments(commentRepository.findAllByItemId(itemId)
                .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));

        if (item.getOwner().getId().equals(userId)) {
            fillInBookingData(itemDto);
        }

        return itemDto;

         */

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item with id:" + itemId + " not found error"));
        ItemDto itemDto = ItemMapper.toItemDto(item);

        itemDto.setComments(commentRepository.findAllByItemId(itemId)
                .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));

        if (!item.getOwner().getId().equals(userId)) {return itemDto;}

        Optional<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"));

        itemDto.setLastBooking(lastBooking.isEmpty() ? null : BookingMapper.toBookingBriefDto(lastBooking.get()));


        Optional<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

        itemDto.setNextBooking(nextBooking.isEmpty() ? null : BookingMapper.toBookingBriefDto(nextBooking.get()));

        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        /*
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> itemDtoList = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

        for (int i = 0; i < itemDtoList.size(); i++) {
            fillInBookingData(itemDtoList.get(i));
            itemDtoList.get(i).setComments(commentRepository.findAllByItemId(itemDtoList.get(i).getId())
                    .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        }

        return itemDtoList;

         */

        User owner = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("user not found"));
        List<Item> repoItems = itemRepository.findAllByOwnerId(userId);
        if (repoItems.isEmpty()) {return new ArrayList<>();}

        List<ItemDto> itemDtoList = repoItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        for (ItemDto itemDto : itemDtoList) {

            List<CommentDto> commentDtos = commentDtos(itemDto.getId());
            itemDto.setComments(commentDtos);

            Optional<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"));

            itemDto.setLastBooking(lastBooking.isEmpty() ? new BookingBriefDto() : BookingMapper.toBookingBriefDto(lastBooking.get()));

            Optional<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

            itemDto.setNextBooking(nextBooking.isEmpty() ? new BookingBriefDto() : BookingMapper.toBookingBriefDto(nextBooking.get()));
        }

        itemDtoList.sort(Comparator.comparing(o -> o.getLastBooking().getStart(),
                Comparator.nullsLast(Comparator.reverseOrder())));

        for (ItemDto itemDto : itemDtoList) {
            if (itemDto.getLastBooking().getBookerId() == null) {
                itemDto.setLastBooking(null);
            }
            if (itemDto.getNextBooking().getBookerId() == null) {
                itemDto.setNextBooking(null);
            }
        }

        return itemDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> findByRequest(String request) {
        if (request == null || request.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> result = new ArrayList<>();
        request = request.toLowerCase();

        for (Item item : itemRepository.findAll()) {
            String name = item.getName().toLowerCase();
            String description = item.getDescription().toLowerCase();

            if (item.getAvailable().equals(true) && (name.contains(request) || description.contains(request))) {
                result.add(item);
            }
        }

        return result;
    }

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item with id:" + itemId + " not found error"));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, BookingStatus.APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("Wrong item for leaving comment");
        }
        Comment comment = CommentMapper.toCommentModel(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    private ItemDto fillInBookingData(ItemDto itemDto) {
        List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId());
        if (!bookings.isEmpty()) {
            itemDto.setLastBooking(BookingMapper.toBookingBriefDto(bookings.get(0)));
        }
        else {
            itemDto.setLastBooking(null);
        }

        if (itemDto.getLastBooking() != null) {
            itemDto.setNextBooking(BookingMapper.toBookingBriefDto(
                    bookingRepository.findAllByItemIdOrderByStartDesc(itemDto.getId()).get(0)));
        }
        else {
            itemDto.setNextBooking(null);
        }

        return itemDto;
    }

    private List<CommentDto> commentDtos(Long itemId) {

        List<Comment> commentList = commentRepository.findAllByItemId(itemId);
        List<CommentDto> commentDtos = commentList.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return commentDtos;
    }
}
