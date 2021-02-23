package com.example.stock.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurerComposite;

@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {

    private AuthManager authManager;

    @Autowired
    public ResourceServerConfig(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http){
        return http.authorizeExchange()
                .pathMatchers("/stock-api/health", "/stock-api/health/**", "/stock-api/api/v1/user/login", "/stock-api/api/v1/user/register").permitAll()
                .pathMatchers("/stock-api/v3/api-docs/**", "/stock-api/webjars/swagger-ui/**", "/stock-api/swagger/**", "/stock-api/swagger-ui.html").permitAll()
                .anyExchange().authenticated()
                .and()
                .authenticationManager(authManager)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebFluxConfigurer corsConfigurer() {
        return new WebFluxConfigurerComposite() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
            }
        };
    }
}
