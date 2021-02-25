package com.example.stock.api;

import com.example.stock.api.dto.ErrorResponse;
import com.example.stock.api.dto.UserDto;
import com.example.stock.api.dto.UserLogin;
import com.example.stock.api.dto.UserLoginResponse;
import com.example.stock.api.model.Role;
import com.example.stock.api.model.User;
import com.example.stock.api.repository.UserRepository;
import com.example.stock.api.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.Collections;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "40000")
@ExtendWith(SpringExtension.class)
@Slf4j
public class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserRepository userRepository;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private UserDto userDto = new UserDto();

    @BeforeEach
    public void init() {
        userDto.setFullName("firstname lastname");
        userDto.setPassword("password");
        userDto.setUsername("user");
        userDto.setRoles(Collections.singletonList(new Role("ADMIN")));
    }

    @Test
    public void testCreateUser() {
        userDto.setUsername("user");
        User user = new User(userDto);
        user.setId(RandomStringUtils.randomAlphabetic(15));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(Mono.just(user));
        UserDto userDto1 = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(contextPath+Constants.API_PREFIX_V1+"/user/register")
                .body(BodyInserters.fromValue(userDto))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UserDto.class)
                .returnResult()
                .getResponseBody();
        log.info("{}", userDto1);
        Assertions.assertNotNull(userDto1);
        Assertions.assertNotNull(userDto1.getId());
    }

    @Test
    public void testCreateUserDuplicateUser() {
        userDto.setUsername("user");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(Mono.error(new DuplicateKeyException("duplicate")));
        ErrorResponse errorResponse = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(contextPath+Constants.API_PREFIX_V1+"/user/register")
                .body(BodyInserters.fromValue(userDto))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();
        log.info("{}", errorResponse);
        Assertions.assertNotNull(errorResponse);
        Assertions.assertNotNull(errorResponse.getDescription());
    }

    @Test
    public void testCreateUserValidation() {
        userDto.setUsername("");
        ErrorResponse errorResponse = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(contextPath+Constants.API_PREFIX_V1+"/user/register")
                .body(BodyInserters.fromValue(userDto))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();
        log.info("{}", errorResponse);
        Assertions.assertNotNull(errorResponse);
        Assertions.assertNotNull(errorResponse.getDescription());
    }

    @Test
    public void testLogin() {
        String password = "password";
        User user = new User(userDto);
        user.setId(RandomStringUtils.randomAlphabetic(15));
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        UserLogin userLogin = new UserLogin("user", password);
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Mono.just(user));
        UserLoginResponse userLoginResponse = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(contextPath+Constants.API_PREFIX_V1+"/user/login")
                .body(BodyInserters.fromValue(userLogin))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UserLoginResponse.class)
                .returnResult()
                .getResponseBody();
        log.info("{}", userLoginResponse);
        Assertions.assertNotNull(userLoginResponse);
        Assertions.assertNotNull(userLoginResponse.getAccessToken());
        Assertions.assertEquals(Constants.TOKEN_PREFIX, userLoginResponse.getTokenType());
    }

    @Test
    public void testLoginWrongPassword() {
        String password = "password";
        User user = new User(userDto);
        user.setId(RandomStringUtils.randomAlphabetic(15));
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        UserLogin userLogin = new UserLogin("user", password+"1");
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Mono.just(user));
        ErrorResponse errorResponse = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(contextPath+Constants.API_PREFIX_V1+"/user/login")
                .body(BodyInserters.fromValue(userLogin))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();
        log.info("{}", errorResponse);
        Assertions.assertNotNull(errorResponse);
        Assertions.assertNotNull(errorResponse.getDescription());
    }

    @Test
    public void testLoginWrongUserNotFound() {
        String password = "password";
        User user = new User(userDto);
        user.setId(RandomStringUtils.randomAlphabetic(15));
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        UserLogin userLogin = new UserLogin("user", password+"1");
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Mono.empty());
        ErrorResponse errorResponse = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(contextPath+Constants.API_PREFIX_V1+"/user/login")
                .body(BodyInserters.fromValue(userLogin))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();
        log.info("{}", errorResponse);
        Assertions.assertNotNull(errorResponse);
        Assertions.assertNotNull(errorResponse.getDescription());
    }
}
