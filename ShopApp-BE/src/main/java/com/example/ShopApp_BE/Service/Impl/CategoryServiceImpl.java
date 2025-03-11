package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.DTO.CategoryDTO;
import com.example.ShopApp_BE.Model.Entity.CategoryEntity;
import com.example.ShopApp_BE.Repository.CategoryRepository;
import com.example.ShopApp_BE.Service.CategoryService;
import com.example.ShopApp_BE.Utils.FileProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileProperties fileProperties;
    @Override
    public CategoryEntity createCategory(CategoryDTO categoryDTO) throws IOException {
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .name(categoryDTO.getName()).build();
        Path uploadPath = Paths.get(fileProperties.getDir());
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = UUID.randomUUID() + "_" + categoryDTO.getImage().getOriginalFilename();
        categoryDTO.getImage().transferTo(new File(fileProperties.getDir() + fileName));
        categoryEntity.setImageUrl(fileProperties.getUrl() + fileName);
        return categoryRepository.save(categoryEntity);
    }
}
