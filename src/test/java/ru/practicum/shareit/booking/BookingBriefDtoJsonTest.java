package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingBriefDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingBriefDtoJsonTest {
    @Autowired
    JacksonTester<BookingBriefDto> json;

    @Test
    @SneakyThrows
    @DisplayName("Тест на проверки перевода в формат JSON для класса BookingBriefDto")
    void bookingBriefDtoJsonTest() {
        BookingBriefDto bookingBriefDto = new BookingBriefDto();
        bookingBriefDto.setId(1L);
        bookingBriefDto.setStart(LocalDateTime.parse("2023-10-01T19:34:50.63"));
        bookingBriefDto.setEnd(LocalDateTime.parse("2023-10-02T19:34:50.63"));

        JsonContent<BookingBriefDto> result = json.write(bookingBriefDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-10-01T19:34:50.63");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-10-02T19:34:50.63");
    }
}
