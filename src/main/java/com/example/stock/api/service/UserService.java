package com.example.stock.api.service;

import com.example.stock.api.config.JwtUtil;
import com.example.stock.api.dto.UserDto;
import com.example.stock.api.dto.UserLogin;
import com.example.stock.api.dto.UserLoginResponse;
import com.example.stock.api.exception.DuplicateEntityException;
import com.example.stock.api.exception.NotFoundException;
import com.example.stock.api.exception.UnauthorizedException;
import com.example.stock.api.model.User;
import com.example.stock.api.repository.UserRepository;
import com.example.stock.api.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Mono<User> findByUsername(String username){
        return userRepository.findByUsername(username)
                .onErrorMap(error->{
                    log.error(error.getMessage(), error);
                    return error;
                })
                .defaultIfEmpty(new User())
                .map(user -> {
                    if (user==null || user.getId()==null || user.getId().isEmpty()){
                        throw new NotFoundException("The user you looking for does not exist!");
                    }
                    return user;
                });
    }

    public Mono<UserDto> create(UserDto userDto){
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(new User(userDto))
                .onErrorMap(error->{
                    if (error instanceof DuplicateKeyException){
                        throw new DuplicateEntityException("A different user already chose this username.", error);
                    }
                    return error;
                })
                .map(UserDto::new);
    }

    public Mono<UserLoginResponse> login(UserLogin userLogin) {
        return findByUsername(userLogin.getUsername())
                .map(user -> {
                    if (!passwordEncoder.matches(userLogin.getPassword(), user.getPassword())){
                        throw new UnauthorizedException("Invalid username/password!");
                    }
                    String token = jwtUtil.generateToken(user);
                    Duration duration = Duration.between(Instant.now(), jwtUtil.getExpiration(token).toInstant());
                    return new UserLoginResponse(token, Constants.TOKEN_PREFIX, String.valueOf(duration.toHours()).concat("hrs"));
                });
    }
}
