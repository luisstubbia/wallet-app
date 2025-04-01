package org.luisstubbia.walletapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.luisstubbia.walletapp.controller.model.UserRequest;
import org.luisstubbia.walletapp.exception.ConflictEntityException;
import org.luisstubbia.walletapp.model.User;
import org.luisstubbia.walletapp.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "User API for creation and authorization")
@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder encoder;

    public UserController(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @Operation(summary = "Creation")
    @ApiResponse(responseCode = "200", description = "Successful user created")
    @PostMapping("/users")
    public User createUser(@RequestBody UserRequest rq) throws ConflictEntityException {
        var usr = User.builder()
                .username(rq.getUsername())
                .password(encoder.encode(rq.getPassword()))
                .email(rq.getEmail())
                .lastname(rq.getLastName())
                .name(rq.getName())
                .build();
        return userService.createUser(usr);
    }
}
