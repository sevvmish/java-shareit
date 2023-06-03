package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {
    private User user;

    @BeforeEach
    private void beforeEach() {
        user = new User(1L, "Ivanov", "ivanov@mail.ru");
    }

    @Test
    @DisplayName("Тест на перевод юзера в специальный объект юзера")
    public void toUserDtoTest() {
        UserDto dto = UserMapper.toUserDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    @DisplayName("Тест на перевод специального объекта юзера в юзера")
    public void toUserModelTest() {
        UserDto dto = new UserDto(1L, "Petrov", "petrov@mail.ru");
        User newUser = UserMapper.toUserModel(dto);

        assertEquals(dto.getId(), newUser.getId());
        assertEquals(dto.getName(), newUser.getName());
        assertEquals(dto.getEmail(), newUser.getEmail());
    }

    @Test
    @DisplayName("Тест на перевод юзеров в специальные объекты юзеров")
    public void toUserDtoListTest() {
        List<User> users = List.of(user);
        List<UserDto> userDtoList = UserMapper.toUserDtoList(users);

        assertFalse(userDtoList.isEmpty());
        assertEquals(users.get(0).getId(), userDtoList.get(0).getId());
        assertEquals(users.get(0).getName(), userDtoList.get(0).getName());
        assertEquals(users.get(0).getEmail(), userDtoList.get(0).getEmail());
    }
}
