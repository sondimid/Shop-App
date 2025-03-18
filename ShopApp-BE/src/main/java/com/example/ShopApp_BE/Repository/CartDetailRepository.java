package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.CartDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartDetailRepository extends JpaRepository<CartDetailEntity, Long> {
    void deleteByCartEntity_Id(Long id);
}
