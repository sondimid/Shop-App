package com.example.ShopApp_BE.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChangePasswordDTO {
    @NotBlank(message = "current password is blank")
    private String currentPassword;

    @NotBlank(message = "new password is blank")
    private String newPassword;

    @NotBlank(message = "confirm password is blank")
    private String confirmPassword;
}
