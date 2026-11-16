package hu.unideb.inf.printingservice.controller;

import hu.unideb.inf.printingservice.service.UserService;
import hu.unideb.inf.printingservice.service.dto.UserDisplayDto;
import hu.unideb.inf.printingservice.service.dto.UserSaveDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDisplayDto saveUser(@Valid @RequestBody UserSaveDto dto) {
        return userService.save(dto);
    }

    @GetMapping
    public List<UserDisplayDto> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDisplayDto getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}