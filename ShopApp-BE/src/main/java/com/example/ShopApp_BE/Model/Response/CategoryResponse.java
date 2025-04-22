package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.CategoryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private Long productQuantity;

    public static CategoryResponse fromCategoryEntity(CategoryEntity categoryEntity) {
        return CategoryResponse.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .imageUrl(categoryEntity.getImageUrl())
                .productQuantity((long) categoryEntity.getProductEntities().size()).build();
    }
}
