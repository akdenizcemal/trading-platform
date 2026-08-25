package com.cemal.trading_platform.controller;


import com.cemal.trading_platform.dto.UserResponse;
import com.cemal.trading_platform.model.User;
import com.cemal.trading_platform.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getBalance()
                ))
                .toList();
    }

    @PostMapping("/users")
    public UserResponse addUser(@RequestBody User user) {
        User savedUser = userService.addUser(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getBalance()
        );
    }
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }
}
