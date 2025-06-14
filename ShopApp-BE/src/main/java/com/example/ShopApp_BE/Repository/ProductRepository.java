package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import com.example.ShopApp_BE.Utils.ConfixSql;
import org.hibernate.query.NativeQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Query(ConfixSql.Product.SEARCH_BY_CATEGORY)
    Page<ProductEntity> findByCategoryEntity_Id(@Param("categoryId") Long categoryId,
                                                @Param("keyword") String keyword,
                                                @Param("fromPrice") Double fromPrice,
                                                @Param("toPrice") Double toPrice,
                                                Pageable pageable);

    boolean existsByName(String name);

    @Query(ConfixSql.Product.SEARCH_BY_KEYWORD)
    Page<ProductEntity> findByKeyword(@Param("keyword") String keyword,
                                      @Param("fromPrice") Double fromPrice,
                                      @Param("toPrice") Double toPrice,
                                      Pageable pageable);

    @Query(ConfixSql.Product.SEARCH_BY_ID)
    Optional<ProductEntity> findById(@Param("id") Long id);

    @Query(ConfixSql.Product.SEARCH)
    List<ProductEntity> findNewest(Pageable pageable);
}
