package com.example.stock.api.controller;

import com.example.stock.api.dto.UserDto;
import com.example.stock.api.dto.UserLogin;
import com.example.stock.api.dto.UserLoginResponse;
import com.example.stock.api.service.UserService;
import com.example.stock.api.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * The type User controller.
 */
@RestController
@RequestMapping(Constants.API_PREFIX_V1+"/user")
public class UserController {

    private UserService userService;

    /**
     * Instantiates a new User controller.
     *
     * @param userService the user service
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register/Create a new user
     *
     * @param userDto the user dto
     * @return the Mono
     */
    @Operation(description = "Register/Create a new user.")
    @PostMapping("/register")
    public Mono<UserDto> create(@RequestBody @Validated UserDto userDto){
        return Mono.create(monoSink -> userService.create(userDto)
                    .onErrorMap(error->{
                        monoSink.error(error);
                        return error;
                    })
                    .subscribe(monoSink::success)
        );
    }

    /**
     * Generate access token for a valid user
     *
     * @param userLogin the user login
     * @return the Mono
     */
    @Operation(description = "Generate access token for a valid user.")
    @PostMapping("/login")
    public Mono<UserLoginResponse> login(@RequestBody @Validated UserLogin userLogin){
        return Mono.create(monoSink -> userService.login(userLogin)
                .onErrorMap(error->{
                    monoSink.error(error);
                    return error;
                })
                .subscribe(monoSink::success)
        );
    }

    /**
     * Get user by username.
     *
     * @param username the username
     * @return the Mono
     */
    @Operation(description = "Get user by username.")
    @GetMapping("/{username}")
    public Mono<UserDto> getUserByUsername(@PathVariable String username){
        return Mono.create(monoSink -> userService.findByUsername(username)
                .onErrorMap(error->{
                    monoSink.error(error);
                    return error;
                })
                .subscribe(user -> monoSink.success(new UserDto(user)))
        );
    }
}
