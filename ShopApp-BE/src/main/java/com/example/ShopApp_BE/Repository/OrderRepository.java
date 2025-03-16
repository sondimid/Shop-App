package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import com.example.ShopApp_BE.Utils.ConfixSql;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByUserEntity_Id(Long userId,Pageable pageable);

//    Page<OrderEntity> findByStatus(String status, Pageable pageable);
//
//    Page<OrderEntity> findByUserEntity_IdAndStatus(Long id, String status, Pageable pageable);

    @Query(ConfixSql.Category.SEARCH_BY_KEYWORD)
    Page<OrderEntity> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(ConfixSql.Category.SEARCH_BY_USER_AND_KEYWORD)
    Page<OrderEntity> findByUserAndKeyword(@Param("userId") Long userId,
                                           @Param("keyword") String keyword, Pageable pageable);
}
