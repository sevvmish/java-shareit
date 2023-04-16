package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDaoInMemoryImpl;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestRestTemplate userRestTemplate;
    private final ItemDaoInMemoryImpl itemStorage;
    private ResponseEntity<Item> response;
    private Item item1;
    private Item item2;
    private User user1;
    private User user2;

    @BeforeEach
    public void beforeEach() {
        item1 = new Item(null, "бензопила", "все люди делятся на две половины", true, null);
        item2 = new Item(null, "кот на час", "много есть и спит", true, null);

        user1 = new User(null, "ivanov", "ivanov@gmail.com");
        user1 = userRestTemplate.postForEntity("/users", user1, User.class).getBody();
        user2 = new User(null, "petrov", "petrov@gmail.com");
        user2 = userRestTemplate.postForEntity("/users", user2, User.class).getBody();
    }

    @Test
    @DisplayName("Тест на добавление нового предмета")
    void createItemTest() {
        response = getPostResponseWithHeader(item1, user1.getId().toString());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(itemStorage.findById(response.getBody().getId()).getName(), response.getBody().getName());
        assertEquals(itemStorage.findById(response.getBody().getId()).getDescription(), response.getBody().getDescription());
    }

    @Test
    @DisplayName("Тест на добавление нового предмета по несуществующему юзеру")
    void createItemWrongUserTest() {
        response = getPostResponseWithHeader(item1, "34");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Тест на обновление предмета")
    void updateItemTest() {
        item1 = getPostResponseWithHeader(item1, user1.getId().toString()).getBody();
        item1.setName("супер бензопила");
        item1.setDescription("абсолютно все люди делятся на две половины");
        response = getPatchResponseWithHeader(item1, user1.getId().toString());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getName(), "супер бензопила");
        assertEquals(response.getBody().getDescription(), "абсолютно все люди делятся на две половины");
    }

    @Test
    @DisplayName("Тест на обновление предмета по чужому юзеру")
    void updateItemWrongUserTest() {
        item1 = getPostResponseWithHeader(item1, user1.getId().toString()).getBody();
        item1.setName("супер бензопила");
        item1.setDescription("абсолютно все люди делятся на две половины");
        response = getPatchResponseWithHeader(item1, user2.getId().toString());
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Тест на получение предмета по ИД")
    void getItemByIdTest() {
        item1 = getPostResponseWithHeader(item1, user1.getId().toString()).getBody();
        ResponseEntity<Item> getItem = restTemplate.getForEntity("/items/" + item1.getId(), Item.class);
        assertEquals(getItem.getStatusCode(), HttpStatus.OK);
        assertEquals(getItem.getBody().getName(), item1.getName());
        assertEquals(getItem.getBody().getDescription(), item1.getDescription());
    }

    @Test
    @DisplayName("Тест на получение всех предметов конкретного юзера")
    void getAllItemsOfUserTest() {
        item1 = getPostResponseWithHeader(item1, user1.getId().toString()).getBody();
        item2 = getPostResponseWithHeader(item2, user1.getId().toString()).getBody();

        Item[] items = getGetResponseWithHeader(user1.getId().toString()).getBody();
        assertEquals(items[0].getName(), item1.getName());
        assertEquals(items[0].getDescription(), item1.getDescription());
        assertEquals(items[1].getName(), item2.getName());
        assertEquals(items[1].getDescription(), item2.getDescription());
    }

    @Test
    @DisplayName("Тест на поиск вещи по текстовому запросу")
    void getAllItemsByRequestTest() {
        item1 = getPostResponseWithHeader(item1, user1.getId().toString()).getBody();
        item2 = getPostResponseWithHeader(item2, user1.getId().toString()).getBody();

        Item[] items = getGetResponseWithHeaderWithRequest(user1.getId().toString(), "покемон").getBody();
        assertEquals(items.length, 0);

        items = getGetResponseWithHeaderWithRequest(user1.getId().toString(), "КОт").getBody();
        assertEquals(items[0].getName(), item2.getName());
        assertEquals(items[0].getDescription(), item2.getDescription());

        items = getGetResponseWithHeaderWithRequest(user1.getId().toString(), "люди").getBody();
        assertEquals(items[0].getName(), item1.getName());
        assertEquals(items[0].getDescription(), item1.getDescription());
    }

    private ResponseEntity<Item> getPostResponseWithHeader(Item item, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId);
        HttpEntity<Item> entity = new HttpEntity<>(item, headers);
        return restTemplate.exchange("/items", HttpMethod.POST, entity, Item.class);
    }

    private ResponseEntity<Item> getPatchResponseWithHeader(Item item, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId);
        HttpEntity<Item> entity = new HttpEntity<>(item, headers);
        return restTemplate.exchange("/items/" + item.getId(), HttpMethod.PATCH, entity, Item.class);
    }

    private ResponseEntity<Item[]> getGetResponseWithHeader(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId);
        return restTemplate.exchange("/items", HttpMethod.GET, new HttpEntity<Object>(headers), Item[].class);
    }

    private ResponseEntity<Item[]> getGetResponseWithHeaderWithRequest(String userId, String request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId);
        return restTemplate.exchange("/items/search?text=" + request, HttpMethod.GET, new HttpEntity<Object>(headers), Item[].class);
    }
}
