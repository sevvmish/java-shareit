package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    private final String userHeaderId = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemResponce;

    @BeforeEach
    public void beforeEach() {
        itemResponce = new ItemRequestDto();
        itemResponce.setId(1L);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест эндпоинта /requests на создание реквеста")
    public void createRequestTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
        itemResponce.setDescription("description");

        when(itemRequestService.create(any(Long.class), any(ItemRequestDto.class)))
                .thenReturn(itemResponce);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header(userHeaderId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponce.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1))
                .create(any(Long.class), any(ItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест эндпоинта /requests на получение всех реквестов по ИД юзера")
    public void getAllByUserIdTest() {
        when(itemRequestService.getAllByUser(any(Long.class)))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests")
                        .header(userHeaderId, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        when(itemRequestService.getAllByUser(any(Long.class)))
                .thenReturn(List.of(itemResponce));

        mvc.perform(get("/requests")
                        .header(userHeaderId, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemResponce))));

        verify(itemRequestService, times(2)).getAllByUser(any(Long.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест эндпоинта /requests на получение всех реквестов")
    public void getAllTest() {
        when(itemRequestService.getAll(any(Integer.class), any(Integer.class), any(Long.class)))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header(userHeaderId, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        when(itemRequestService.getAll(any(Integer.class), any(Integer.class), any(Long.class)))
                .thenReturn(List.of(itemResponce));

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header(userHeaderId, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemResponce))));

        verify(itemRequestService, times(2))
                .getAll(any(Integer.class), any(Integer.class), any(Long.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест эндпоинта /requests на получение реквеста по ИД")
    public void getByIdTest() {
        itemResponce.setItems(Collections.emptyList());
        when(itemRequestService.getById(any(Long.class), any(Long.class)))
                .thenReturn(itemResponce);

        mvc.perform(get("/requests/1")
                        .header(userHeaderId, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponce.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemResponce.getDescription()), String.class))
                .andExpect(jsonPath("$.items", is(itemResponce.getItems()), List.class));

        verify(itemRequestService, times(1)).getById(any(Long.class), any(Long.class));
    }
}
