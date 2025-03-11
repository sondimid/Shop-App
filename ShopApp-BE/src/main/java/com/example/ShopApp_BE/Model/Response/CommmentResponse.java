package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.CommentEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommmentResponse extends AbstractResponse{
    private UserResponse userResponse;

    private String content;

    public static CommmentResponse fromCommentEntity(CommentEntity commentEntity) {
        return CommmentResponse.builder()
                .userResponse(UserResponse.fromUserEntity(commentEntity.getUserEntity()))
                .content(commentEntity.getContent()).build();
    }
}
