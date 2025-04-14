package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.CartDetailEntity;
import com.example.ShopApp_BE.Utils.ConfixSql;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartDetailRepository extends JpaRepository<CartDetailEntity, Long> {
    void deleteByCartEntity_Id(Long id);

    @Query(ConfixSql.CartDetail.SEARCH_BY_CART)
    List<CartDetailEntity> findByCartEntity_Id(@Param("cartId") Long cartId);
}
