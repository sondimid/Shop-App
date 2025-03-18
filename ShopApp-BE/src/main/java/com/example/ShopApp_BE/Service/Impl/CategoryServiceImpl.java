package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.DTO.CategoryDTO;
import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.Model.Entity.CategoryEntity;
import com.example.ShopApp_BE.Model.Response.CategoryResponse;
import com.example.ShopApp_BE.Repository.CategoryRepository;
import com.example.ShopApp_BE.Service.CategoryService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.UploadImages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.channels.NotYetBoundException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public CategoryEntity createCategory(CategoryDTO categoryDTO) throws IOException {
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .name(categoryDTO.getName()).build();
        categoryEntity.setImageUrl(UploadImages.uploadImage(categoryDTO.getImage()));
        return categoryRepository.save(categoryEntity);
    }

    @Override
    public CategoryEntity updateCategory(CategoryDTO categoryDTO, Long id) throws Exception {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageKeys.CATEGORY_NOT_FOUND));
        categoryEntity.setName(categoryDTO.getName());
        categoryEntity.setImageUrl(UploadImages.uploadImage(categoryDTO.getImage()));
        return categoryRepository.save(categoryEntity);
    }

    @Override
    public List<CategoryResponse> getAll() {
        List<CategoryEntity> categoryEntities = categoryRepository.findAll();
        return categoryEntities.stream().map(CategoryResponse::fromCategoryEntity).toList();
    }

    @Override
    public String deleteById(Long id) throws Exception {
        if(!categoryRepository.existsById(id)) {
            throw new NotFoundException(MessageKeys.CATEGORY_NOT_FOUND);
        }
        categoryRepository.deleteById(id);
        return MessageKeys.DELETE_CATEGORY_SUCCESS;
    }
}
