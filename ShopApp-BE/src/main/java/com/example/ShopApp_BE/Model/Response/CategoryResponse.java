package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.CategoryEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
                .productQuantity((long) 2).build();
    }
}
