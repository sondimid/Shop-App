package com.example.ShopApp_BE.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder

public class UserRegisterDTO {
    @NotBlank(message = "full name is blank")
    private String fullName;

    @NotBlank(message = "email is blank")
    private String email;

    @NotBlank(message = "phone number is blank")
    private String phoneNumber;

    @NotBlank(message = "password is blank")
    private String password;

    @NotBlank(message = "confirm password is blank")
    private String confirmPassword;

}
