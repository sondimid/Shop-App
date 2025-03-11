package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.*;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}
