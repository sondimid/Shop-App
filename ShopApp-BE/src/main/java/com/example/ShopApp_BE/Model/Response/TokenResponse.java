package com.example.ShopApp_BE.Model.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {
    private String accessToken;

    private String refreshToken;

    private Long userId;
}
