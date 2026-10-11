
package com.cemal.trading_platform.controller;

import com.cemal.trading_platform.dto.UserResponse;
import com.cemal.trading_platform.model.User;
import com.cemal.trading_platform.service.UserService;
import org.springframework.web.bind.annotation.*;
import com.cemal.trading_platform.dto.RegisterRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse addUser(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setBalance(new BigDecimal("10000.00"));

        User savedUser = userService.addUser(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getBalance()
        );
    }
}