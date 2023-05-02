package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDaoInMemoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private final UserDaoInMemoryImpl userStorage;
    private ResponseEntity<User> response;
    private User user1;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(null, "ivanov", "ivanov@gmail.com");
    }

    @AfterEach
    public void afterEach() {
        userStorage.clearDataForTesting();
    }

    @Test
    @DisplayName("Тест на добавление нового юзера")
    void createUserTest() {
        User user = new User(null, "petrov", "petro@gmail.com");
        response = getPostResponse(user);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getName(), "petrov");
        assertEquals(response.getBody().getEmail(), "petro@gmail.com");
    }

    @Test
    @DisplayName("Тест на добавление юзера с неправильными именем/имейлом")
    void createWrongUserTest() {
        User user = new User(null, "", "petro@gmail.com");
        response = getPostResponse(user);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        user.setName("petrov");
        user.setEmail("petrogmail.com");

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на обновление юзера")
    void updateUserTest() {
        user1 = getPostResponse(user1).getBody();

        user1.setName("notPetrov");
        user1.setEmail("notpetro@gmail.com");

        response = getPatchResponse(user1);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getName(), "notPetrov");
        assertEquals(response.getBody().getEmail(), "notpetro@gmail.com");
    }

    @Test
    @DisplayName("Тест на получения юзера по ИД")
    void getUserByIdTest() {
        user1 = getPostResponse(user1).getBody();

        ResponseEntity<User> findUser = restTemplate.getForEntity("/users/" + user1.getId(), User.class);
        assertEquals(findUser.getStatusCode(), HttpStatus.OK);
        assertEquals(findUser.getBody(), user1);
    }

    @Test
    @DisplayName("Тест на получения всех юзеров")
    void getAllUsersTest() {
        user1 = getPostResponse(user1).getBody();
        User user2 = new User(null, "sidorov", "sidorov@gmail.com");
        user2 = getPostResponse(user2).getBody();
        User[] users = new User[]{user1, user2};

        ResponseEntity<User[]> responseList = restTemplate.getForEntity("/users", User[].class);
        User[] usersFromRequest = responseList.getBody();

        for (int i = 0; i < users.length; i++) {
            assertEquals(users[i], usersFromRequest[i]);
        }
    }

    @Test
    @DisplayName("Тест на удаление юзера по ИД")
    void deleteUserByIdTest() {
        user1 = getPostResponse(user1).getBody();
        HttpEntity<User> entity = new HttpEntity<>(user1);
        response = restTemplate.exchange("/users/" + user1.getId(), HttpMethod.DELETE, entity, User.class);

        ResponseEntity<User> findUser = restTemplate.getForEntity("/users/" + user1.getId(), User.class);
        assertEquals(findUser.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<User> getPostResponse(User user) {
        return restTemplate.postForEntity("/users", user, User.class);
    }

    private ResponseEntity<User> getPatchResponse(User user) {
        HttpEntity<User> entity = new HttpEntity<>(user);
        return restTemplate.exchange("/users/" + user.getId(), HttpMethod.PATCH, entity, User.class, user.getId());
    }
}
