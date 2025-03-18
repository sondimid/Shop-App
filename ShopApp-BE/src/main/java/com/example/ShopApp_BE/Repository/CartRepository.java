package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
}
