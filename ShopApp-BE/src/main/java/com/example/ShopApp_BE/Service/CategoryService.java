package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.DTO.CategoryDTO;
import com.example.ShopApp_BE.Model.Entity.CategoryEntity;
import com.example.ShopApp_BE.Model.Response.CategoryResponse;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

public interface CategoryService {
    CategoryEntity createCategory(CategoryDTO categoryDTO) throws IOException;

    CategoryEntity updateCategory(CategoryDTO categoryDTO, Long id) throws Exception;

    List<CategoryResponse> getAll();

    String deleteById(Long id) throws Exception;
}
