package com.example.stock.api;

import com.example.stock.api.dto.*;
import com.example.stock.api.model.Role;
import com.example.stock.api.model.Stock;
import com.example.stock.api.model.User;
import com.example.stock.api.repository.StockRepository;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "40000")
@ExtendWith(SpringExtension.class)
@Slf4j
public class StockControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private StockRepository stockRepository;

    @MockBean
    private UserRepository userRepository;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private StockDto stockDto = new StockDto();
    private String accessToken;

    @BeforeEach
    public void init() {
        stockDto.setAmountPaid(100.0);
        stockDto.setPriceBought(10.0);
        stockDto.setVolumeBought(10.0);
        stockDto.setAmountPaid(10.0);
        stockDto.setSymbol("TST");
        stockDto.setCompanyName("Test INC");
        stockDto.setPrimaryExchange("Test ex");
        stockDto.setUsername("user");

        UserDto userDto = new UserDto();
        userDto.setFullName("firstname lastname");
        userDto.setPassword("password");
        userDto.setUsername("user");
        userDto.setRoles(Collections.singletonList(new Role("ADMIN")));

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
        Assertions.assertNotNull(userLoginResponse);
        accessToken = userLoginResponse.getAccessToken();
    }

    @Test
    public void testSaveStock() {
        Stock stock = new Stock(stockDto);
        stock.setId(RandomStringUtils.randomAlphabetic(15));
        Mockito.when(stockRepository.save(Mockito.any(Stock.class))).thenReturn(Mono.just(stock));
        StockDto stockDto1 = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(contextPath+Constants.API_PREFIX_V1+"/stock")
                .header(HttpHeaders.AUTHORIZATION, Constants.TOKEN_PREFIX.concat(accessToken))
                .body(BodyInserters.fromValue(stockDto))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(StockDto.class)
                .returnResult()
                .getResponseBody();
        log.info("{}", stockDto1);
        Assertions.assertNotNull(stockDto1);
        Assertions.assertNotNull(stockDto1.getId());
    }

    @Test
    public void testGetStocks() {
        Stock stock = new Stock(stockDto);
        stock.setId(RandomStringUtils.randomAlphabetic(15));
        Mockito.when(stockRepository.findAllByUsername(Mockito.any(String.class))).thenReturn(Flux.just(stock, stock, stock));
        List<StockDto> stockDtos = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(contextPath+Constants.API_PREFIX_V1+"/stock/me")
                .header(HttpHeaders.AUTHORIZATION, Constants.TOKEN_PREFIX.concat(accessToken))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(new ParameterizedTypeReference<List<StockDto>>(){})
                .returnResult()
                .getResponseBody();
        log.info("{}", stockDtos);
        Assertions.assertNotNull(stockDtos);
        Assertions.assertFalse(stockDtos.isEmpty());
        Assertions.assertEquals(3, stockDtos.size());
    }

    @Test
    public void testGetQuote() {
        Stock stock = new Stock(stockDto);
        stock.setId(RandomStringUtils.randomAlphabetic(15));
        Mockito.when(stockRepository.findAllByUsername(Mockito.any(String.class))).thenReturn(Flux.just(stock, stock, stock));
        StockQuote stockQuote = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(contextPath+Constants.API_PREFIX_V1+"/stock?symbol=NFLX")
                .header(HttpHeaders.AUTHORIZATION, Constants.TOKEN_PREFIX.concat(accessToken))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(StockQuote.class)
                .returnResult()
                .getResponseBody();
        log.info("{}", stockQuote);
        Assertions.assertNotNull(stockQuote);
    }
}
