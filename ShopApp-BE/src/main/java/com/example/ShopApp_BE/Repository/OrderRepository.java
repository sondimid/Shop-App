package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByUserEntity_Id(Long userId,Pageable pageable);
}
