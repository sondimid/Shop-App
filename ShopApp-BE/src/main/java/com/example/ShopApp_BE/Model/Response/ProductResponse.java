package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.CommentEntity;
import com.example.ShopApp_BE.Model.Entity.ImageEntity;
import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse extends AbstractResponse {
    private Long id;

    private String name;

    private String description;

    private Double price;

    private String category;

    private Double discount;

    private List<ImageResponse> imageResponses;

    private List<CommentResponse> comments;

    public static ProductResponse fromProductEntity(ProductEntity productEntity) {
        ProductResponse productResponse =  ProductResponse.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .description(productEntity.getDescription())
                .category(productEntity.getCategoryEntity().getName())
                .discount(productEntity.getDiscount())
                .imageResponses(productEntity.getImageEntities().stream().map(ImageResponse::fromImageEntity).toList())
                .comments(productEntity.getCommentEntities().stream().map(CommentResponse::fromCommentEntity).toList())
                .build();
        productResponse.setCreatedAt(productEntity.getCreatedAt());
        productResponse.setUpdatedAt(productEntity.getUpdatedAt());
        return productResponse;
    }
}
