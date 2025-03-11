package com.example.ShopApp_BE.Service.Impl;


import com.example.ShopApp_BE.Model.Entity.TokenEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.TokenResponse;
import com.example.ShopApp_BE.Repository.TokenRepository;
import com.example.ShopApp_BE.Repository.UserRepository;
import com.example.ShopApp_BE.Service.TokenService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        TokenEntity tokenEntity = tokenRepository.findByRefreshToken(refreshToken);
        if(tokenEntity == null || !jwtTokenUtils.isNotExpired(refreshToken, TokenType.REFRESH)) {
            throw new NullPointerException(MessageKeys.REFRESH_TOKEN_INVALID);
        }
        UserEntity userEntity = tokenEntity.getUserEntity();
        String accessToken = jwtTokenUtils.generateToken(userEntity, TokenType.ACCESS);
        tokenEntity.setAccessToken(accessToken);
        tokenRepository.save(tokenEntity);
        return TokenResponse.fromTokenEntity(tokenEntity);
    }

    @Override
    public String deleteToken(String accessToken) {
        tokenRepository.deleteByAccessToken(accessToken);
        return MessageKeys.DELETE_TOKEN_SUCCESS;
    }
}
