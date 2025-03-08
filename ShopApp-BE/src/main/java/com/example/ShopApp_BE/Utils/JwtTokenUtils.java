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
    private final String REFRESH_TOKEN = "refresh_token";
    private final String ACCESS_TOKEN = "access_token";

    public String generateToken(UserEntity userEntity, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneNumber", userEntity.getPhoneNumber());
        String token;
        if(tokenType.equals(REFRESH_TOKEN)) {
            token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationRefreshToken() * 1000))
                    .setSubject(userEntity.getPhoneNumber())
                    .signWith(getSignInKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
                    .compact();
        }
        else{
            token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration() * 1000))
                    .setSubject(userEntity.getPhoneNumber())
                    .signWith(getSignInKey(ACCESS_TOKEN), SignatureAlgorithm.HS256)
                    .compact();
        }
        return token;
    }

    private Claims extractAllClaims(String token, String tokenType) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey(tokenType))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(String tokenType){
        byte[] bytes;
        if(tokenType.equals(ACCESS_TOKEN)){
            bytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        }
        else bytes = Decoders.BASE64.decode(jwtProperties.getRefreshKey());
        return Keys.hmacShaKeyFor(bytes);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String tokenType) {
        final Claims claims = extractAllClaims(token, tokenType);
        return claimsResolver.apply(claims);
    }

    public String extractPhoneNumber(String token, String tokenType) {
        return extractClaim(token, Claims::getSubject, tokenType);
    }

    public Boolean isNotExpired(String token, String tokenType) {
        Date expiration = extractClaim(token, Claims::getExpiration, tokenType);
        return expiration.after(new Date());
    }

    public Boolean isValid(String token, UserEntity userEntity, String tokenType) {
        String phoneNumber = extractPhoneNumber(token, tokenType);
        return userEntity.getPhoneNumber().equals(phoneNumber) && isNotExpired(token, tokenType);
    }

}
