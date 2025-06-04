package com.example.ShopApp_BE.Model.Response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse{
    private String accessToken;
}
