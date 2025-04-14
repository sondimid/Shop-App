package com.example.ShopApp_BE.Utils;

import com.example.ShopApp_BE.Model.Entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    private final JwtProperties jwtProperties;

    public String generateToken(UserEntity userEntity, TokenType tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userEntity.getEmail());
        claims.put("role", userEntity.getRoleEntity().getRole());
        claims.put("id", userEntity.getId());
        String token;
        if(tokenType.equals(TokenType.REFRESH)) {
            token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationRefreshToken() * 1000))
                    .setSubject(userEntity.getPhoneNumber())
                    .signWith(getSignInKey(tokenType), SignatureAlgorithm.HS256)
                    .compact();
        }
        else if (tokenType.equals(TokenType.ACCESS)){
            token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration() * 1000))
                    .setSubject(userEntity.getPhoneNumber())
                    .signWith(getSignInKey(tokenType), SignatureAlgorithm.HS256)
                    .compact();
        }
        else {
            token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationResetToken() * 1000))
                    .setSubject(userEntity.getPhoneNumber())
                    .signWith(getSignInKey(tokenType), SignatureAlgorithm.HS256)
                    .compact();
        }
        return token;
    }

    private Claims extractAllClaims(String token, TokenType tokenType) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey(tokenType))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(TokenType tokenType){
        byte[] bytes;
        if(tokenType.equals(TokenType.ACCESS)){
            bytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        }
        else if (tokenType.equals(TokenType.REFRESH)) bytes = Decoders.BASE64.decode(jwtProperties.getRefreshKey());
        else bytes = Decoders.BASE64.decode(jwtProperties.getResetKey());
        return Keys.hmacShaKeyFor(bytes);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, TokenType tokenType) {
        final Claims claims = extractAllClaims(token, tokenType);
        return claimsResolver.apply(claims);
    }

    public String extractEmail(String token, TokenType tokenType) {
        return extractClaim(token, claims -> claims.get("email", String.class), tokenType);
    }

    public String extractRole(String token, TokenType tokenType) {
        return extractClaim(token, claims -> claims.get("role", String.class), tokenType);
    }

    public Long extractId(String token, TokenType tokenType) {
        return extractClaim(token, claims -> claims.get("id", Long.class), tokenType);
    }

    public Boolean isNotExpired(String token, TokenType tokenType) {
        Date expiration = extractClaim(token, Claims::getExpiration, tokenType);
        return expiration.after(new Date());
    }

    public Boolean isValid(String token, UserEntity userEntity, TokenType tokenType) {
        String email = extractEmail(token, tokenType);
        return userEntity.getEmail().equals(email) && isNotExpired(token, tokenType);
    }

}
