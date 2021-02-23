package com.example.stock.api.config;

import com.example.stock.api.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthManager implements ReactiveAuthenticationManager {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        if (!jwtUtil.isTokenExpired(authToken)) {
            throw new UnauthorizedException("Your token has expired!");
        }
        return Mono.just(new UsernamePasswordAuthenticationToken(jwtUtil.getSubject(authToken), null, jwtUtil.getRoles(authToken)));
    }
}
