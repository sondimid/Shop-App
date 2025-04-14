package com.example.ShopApp_BE.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
public class UserRegisterDTO {
    @NotBlank(message = "Full name is blank")
    private String fullName;

    @NotBlank(message = "Email is blank")
    private String email;

    @NotBlank(message = "Password is blank")
    private String password;

    @NotBlank(message = "Confirm password is blank")
    private String confirmPassword;

}
