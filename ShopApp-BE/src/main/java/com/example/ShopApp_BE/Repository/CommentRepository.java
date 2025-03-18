package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findByProductEntity_Id(Long productId, Pageable pageable);

    Page<CommentEntity> findByUserEntity_Id(Long id, Pageable pageable);
}
