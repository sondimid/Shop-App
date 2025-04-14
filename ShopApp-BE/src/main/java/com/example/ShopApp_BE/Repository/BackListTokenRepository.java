package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.BlackListTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface BackListTokenRepository extends JpaRepository<BlackListTokenEntity, Long> {
    boolean existsByAccessToken(String token);
}
