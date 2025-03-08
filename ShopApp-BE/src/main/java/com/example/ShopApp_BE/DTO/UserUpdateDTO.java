package com.example.ShopApp_BE.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserUpdateDTO {
    private String email;

    private String address;

    private String dateOfBirth;

}
