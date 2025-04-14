package com.example.ShopApp_BE.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserVerifyDTO {
    private String email;

    private String otp;
}
