package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.TokenEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    TokenEntity findByUserEntity(UserEntity userEntity);
    TokenEntity findByRefreshToken(String refreshToken);
    void deleteByAccessToken(String accessToken);
    TokenEntity findByAccessToken(String accessToken);
    Boolean existsByAccessToken(String token);
}
