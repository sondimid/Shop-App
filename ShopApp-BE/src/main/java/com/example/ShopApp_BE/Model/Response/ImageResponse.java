package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.ImageEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageResponse extends AbstractResponse{
    private Long id;

    private String url;

    public static ImageResponse fromImageEntity(ImageEntity imageEntity) {
        ImageResponse imageResponse = ImageResponse.builder()
                .id(imageEntity.getId())
                .url(imageEntity.getUrl()).build();
        imageResponse.setCreatedAt(imageEntity.getCreatedAt());
        imageResponse.setUpdatedAt(imageEntity.getUpdatedAt());
        return imageResponse;
    }
}
