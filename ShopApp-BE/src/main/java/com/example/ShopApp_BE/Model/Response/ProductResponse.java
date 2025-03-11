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

    private Long categoryId;

    private Double discount;

    private List<ImageResponse> imageResponses;

    private List<CommmentResponse> comments;

    public static ProductResponse fromProductEntity(ProductEntity productEntity) {
        ProductResponse productResponse =  ProductResponse.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .description(productEntity.getDescription())
                .categoryId(productEntity.getCategoryEntity().getId())
                .discount(productEntity.getDiscount())
                .imageResponses(productEntity.getImageEntities().stream().map(ImageResponse::fromImageEntity).toList())
                .comments(productEntity.getCommentEntities().stream().map(CommmentResponse::fromCommentEntity).toList())
                .build();
        productResponse.setCreatedAt(productEntity.getCreatedAt());
        productResponse.setUpdatedAt(productEntity.getUpdatedAt());
        return productResponse;
    }
}
