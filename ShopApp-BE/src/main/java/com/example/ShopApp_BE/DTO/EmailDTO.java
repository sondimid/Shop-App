package com.example.ShopApp_BE.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDTO {
    @NotBlank(message = "Email is blank!")
    private String email;
}
