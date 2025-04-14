package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.CartEntity;
import com.example.ShopApp_BE.Utils.ConfixSql;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
}
