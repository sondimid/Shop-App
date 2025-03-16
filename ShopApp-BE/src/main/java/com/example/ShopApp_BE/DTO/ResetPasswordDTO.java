package com.example.ShopApp_BE.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDTO {
    private String resetToken;

    private String newPassword;

    private String confirmPassword;
}
