package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.ImageEntity;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImageResponse extends AbstractResponse implements Serializable {
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
