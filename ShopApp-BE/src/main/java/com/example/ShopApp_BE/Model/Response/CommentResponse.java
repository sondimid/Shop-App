package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.CommentEntity;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse extends AbstractResponse implements Serializable {
    private String userName;

    private String avatar;

    private Long productId;

    private String content;

    private String image;

    public static CommentResponse fromCommentEntity(CommentEntity commentEntity) {
        CommentResponse commentResponse = CommentResponse.builder()
                .userName(commentEntity.getUserEntity().getFullName())
                .avatar(commentEntity.getUserEntity().getAvatarUrl())
                .productId(commentEntity.getProductEntity().getId())
                .image(commentEntity.getImageUrl())
                .content(commentEntity.getContent()).build();
        commentResponse.setCreatedAt(commentEntity.getCreatedAt());
        commentResponse.setUpdatedAt(commentEntity.getUpdatedAt());
        return commentResponse;
    }
}
