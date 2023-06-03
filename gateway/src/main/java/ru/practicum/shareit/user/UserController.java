package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@NotNull @PathVariable Long userId) {
        return userClient.findById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@NotNull @PathVariable Long userId,
                                         @RequestBody UserDto userDto) {
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@NotNull @PathVariable Long userId) {
        userClient.delete(userId);
    }
}
