package com.example.ShopApp_BE.Service.Impl;


import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.Model.Entity.BlackListTokenEntity;
import com.example.ShopApp_BE.Model.Entity.TokenEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.TokenResponse;
import com.example.ShopApp_BE.Repository.BackListTokenRepository;
import com.example.ShopApp_BE.Repository.TokenRepository;
import com.example.ShopApp_BE.Service.TokenService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final BackListTokenRepository backListTokenRepository;

    @Override
    public TokenResponse refreshToken(String refreshToken) throws Exception {
        TokenEntity tokenEntity = tokenRepository.findByRefreshToken(refreshToken);
        if(tokenEntity == null || !jwtTokenUtils.isNotExpired(refreshToken, TokenType.REFRESH)) {
            throw new Exception(MessageKeys.REFRESH_TOKEN_INVALID);
        }
        UserEntity userEntity = tokenEntity.getUserEntity();
        String accessToken = jwtTokenUtils.generateToken(userEntity, TokenType.ACCESS);
        tokenRepository.save(tokenEntity);
        return TokenResponse.fromTokenEntity(tokenEntity, accessToken);
    }

    @Override
    public void deleteToken(String accessToken, String refreshToken) throws Exception {
        TokenEntity tokenEntity = tokenRepository.findByRefreshToken(refreshToken);
        if(tokenEntity == null || !jwtTokenUtils.isNotExpired(refreshToken, TokenType.REFRESH)) {
            throw new NotFoundException(MessageKeys.REFRESH_TOKEN_INVALID);
        }
        BlackListTokenEntity token = BlackListTokenEntity.builder()
                .accessToken(accessToken).build();
        backListTokenRepository.save(token);
        tokenRepository.delete(tokenEntity);
    }


}
