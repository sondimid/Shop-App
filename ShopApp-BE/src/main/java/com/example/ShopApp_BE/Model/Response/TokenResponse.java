package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.TokenEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse extends AbstractResponse{
    private String accessToken;

    private String refreshToken;

    private Boolean revoke;

    private Long userId;

    public static TokenResponse fromTokenEntity(TokenEntity tokenEntity, String accessToken) {
        TokenResponse tokenResponse = TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(tokenEntity.getRefreshToken())
            .revoke(tokenEntity.getRevoked())
            .userId(tokenEntity.getUserEntity().getId())
            .build();
        tokenResponse.setCreatedAt(tokenEntity.getCreatedAt());
        tokenResponse.setUpdatedAt(tokenEntity.getUpdatedAt());
        return tokenResponse;
    }
}
