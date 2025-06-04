package com.example.ShopApp_BE.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVerifyDTO {
    private String email;

    private String otp;
}
