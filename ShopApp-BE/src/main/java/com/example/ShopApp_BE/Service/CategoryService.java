package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.DTO.CategoryDTO;
import com.example.ShopApp_BE.Model.Entity.CategoryEntity;
import jakarta.validation.Valid;

import java.io.IOException;

public interface CategoryService {
    CategoryEntity createCategory(CategoryDTO categoryDTO) throws IOException;
}
