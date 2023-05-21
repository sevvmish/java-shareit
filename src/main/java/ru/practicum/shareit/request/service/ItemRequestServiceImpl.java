package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestDtos.forEach(this::setItemsToItemRequestDto);

        return itemRequestDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAll(int from, int size, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByRequestorNotLikeOrderByCreatedAsc(user,
                        PageRequest.of(from, size))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestDtos.forEach(this::setItemsToItemRequestDto);

        return itemRequestDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("request with id:" + requestId + " not found error"));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        setItemsToItemRequestDto(itemRequestDto);

        return itemRequestDto;
    }

    private void setItemsToItemRequestDto(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
    }
}
