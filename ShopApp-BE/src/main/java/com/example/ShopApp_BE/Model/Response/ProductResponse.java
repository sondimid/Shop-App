package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.CommentEntity;
import com.example.ShopApp_BE.Model.Entity.ImageEntity;
import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductResponse extends AbstractResponse implements Serializable {
    private Long id;

    private String name;

    private String description;

    private Double price;

    private String category;

    private Long categoryId;

    private Double discount;

    private Long quantity;

    private Double finalPrice;

    private List<ImageResponse> imageResponses;

    private List<CommentResponse> comments;

    public static ProductResponse fromProductEntity(ProductEntity productEntity) {
        ProductResponse productResponse =  ProductResponse.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .finalPrice(Math.round(productEntity.getFinalPrice() * 100.0) / 100.0)
                .description(productEntity.getDescription())
                .category(productEntity.getCategoryEntity().getName())
                .discount(productEntity.getDiscount())
                .categoryId(productEntity.getCategoryEntity().getId())
                .quantity(productEntity.getQuantity())
                .imageResponses(productEntity.getImageEntities().stream().map(ImageResponse::fromImageEntity).toList())
                .comments(productEntity.getCommentEntities().stream().sorted(Comparator.comparing(CommentEntity::getCreatedAt).reversed()).map(CommentResponse::fromCommentEntity).toList())
                .build();
        productResponse.setCreatedAt(productEntity.getCreatedAt());
        productResponse.setUpdatedAt(productEntity.getUpdatedAt());
        return productResponse;
    }
}
