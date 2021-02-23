package com.example.stock.api.config;

import com.example.stock.api.model.Role;
import com.example.stock.api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtUtil {

    private int expiresAt;
    private final Key key;

    @Autowired
    public JwtUtil(@Value("${jwt.secret}")String jwtSecret, @Value("${jwt.expiresAt}") int expiresAt) {
        this.expiresAt = expiresAt;
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(User user){
        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+(expiresAt*1000)))
                .signWith(key)
                .compact();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpiration(String token){
        return getAllClaimsFromToken(token).getExpiration();
    }

    public List<GrantedAuthority> getRoles(String token){
        List<GrantedAuthority> grantedAuthorities=new ArrayList<>();
        List<String> roles = getAllClaimsFromToken(token).get("roles", List.class);
        roles.forEach(role->grantedAuthorities.add(new SimpleGrantedAuthority(role)));
        return grantedAuthorities;
    }

    public String getSubject(String token){
        return getAllClaimsFromToken(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = getExpiration(token);
        return expiration.before(new Date());
    }
}
