package com.example.ShopApp_BE.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserLoginDTO implements Serializable {
    @NotBlank(message = "Email is blank")
    private String email;

    @NotBlank(message = "password is blank")
    private String password;

}
