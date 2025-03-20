package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    List<UserEntity> findByIdIn(List<Long> ids);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByResetToken(String resetToken);

    boolean existsByGoogleAccountId(String googleAccountId);

    Optional<UserEntity> findByGoogleAccountId(String googleAccountId);
}
