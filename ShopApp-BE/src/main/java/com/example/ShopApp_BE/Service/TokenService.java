package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.DTO.RefreshTokenDTO;
import com.example.ShopApp_BE.Model.Response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;



public interface TokenService {
    TokenResponse refreshToken(String refreshToken) throws Exception;

    void deleteToken(String accessToken, String refreshToken) throws Exception;
}
