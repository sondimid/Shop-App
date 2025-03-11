package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
}
