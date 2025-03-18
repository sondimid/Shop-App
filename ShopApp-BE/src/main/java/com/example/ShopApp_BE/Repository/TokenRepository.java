package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.TokenEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    TokenEntity findByRefreshToken(String refreshToken);
}
