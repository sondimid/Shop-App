package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
