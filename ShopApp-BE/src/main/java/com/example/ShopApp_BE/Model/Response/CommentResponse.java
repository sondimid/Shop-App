package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.CommentEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse extends AbstractResponse {
    private Long userId;

    private Long productId;

    private String content;

    public static CommentResponse fromCommentEntity(CommentEntity commentEntity) {
        CommentResponse commentResponse = CommentResponse.builder()
                .userId(commentEntity.getUserEntity().getId())
                .productId(commentEntity.getProductEntity().getId())
                .content(commentEntity.getContent()).build();
        commentResponse.setCreatedAt(commentEntity.getCreatedAt());
        commentResponse.setUpdatedAt(commentEntity.getUpdatedAt());
        return commentResponse;
    }
}
