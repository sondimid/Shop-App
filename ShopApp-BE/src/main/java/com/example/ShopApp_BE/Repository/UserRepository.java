package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Utils.ConfixSql;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByGoogleAccountId(String googleAccountId);

    Optional<UserEntity> findByGoogleAccountId(String googleAccountId);

    @Query(ConfixSql.User.SEARCH_BY_KEYWORD)
    Page<UserEntity> findByKeyword(@Param("keyword") String keyword, PageRequest pageRequest);
}
