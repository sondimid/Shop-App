package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import com.example.ShopApp_BE.Utils.ConfixSql;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Query(ConfixSql.Product.SEARCH_BY_CATEGORY)
    Page<ProductEntity> findByCategoryEntity_Id(@Param("categoryId") Long categoryId, Pageable pageable);
}
