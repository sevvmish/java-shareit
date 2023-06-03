package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    private void beforeEach() {
        user = new User(1L, "Ivanov", "ivanov@mail.ru");
    }

    @Test
    @DisplayName("Тест на создание нового пользователя")
    void createTest() {
        User savedUser = new User();
        savedUser.setName(user.getName());
        savedUser.setEmail(user.getEmail());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User newUser = userService.create(savedUser);

        assertEquals(1, newUser.getId());
        assertEquals(savedUser.getName(), newUser.getName());
        assertEquals(savedUser.getEmail(), newUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Тест на обновление пользователя")
    void updateTest() {
        user.setName("updated name");
        UserDto inputDto = UserMapper.toUserDto(user);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        User userDto = userService.update(1, inputDto);

        assertEquals(userDto.getId(), 1);
        assertEquals(userDto.getName(), inputDto.getName());
    }

    @Test
    @DisplayName("Тест на поиск пользователя по ИД")
    void findByIdTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));

        User userDto = userService.findById(1);

        assertEquals(1, userDto.getId());

        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("Тест на удаление пользователя по ИД")
    void deleteByIdTest() {
        userService.delete(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Тест на получение всех пользователей")
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAll();

        assertFalse(users.isEmpty());
        assertEquals(user.getId(), users.get(0).getId());

        verify(userRepository, times(1)).findAll();
    }
}
