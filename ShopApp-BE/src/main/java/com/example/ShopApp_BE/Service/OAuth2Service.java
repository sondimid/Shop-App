package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.TokenResponse;
import com.example.ShopApp_BE.Utils.OAuth2Provider;

import java.io.IOException;

public interface OAuth2Service {
    TokenResponse getUserInfoFromSocial(String code, OAuth2Provider provider);
}
