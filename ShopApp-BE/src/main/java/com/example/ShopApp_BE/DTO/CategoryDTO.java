package com.example.ShopApp_BE.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class CategoryDTO {
    @NotBlank(message = "name is bank")
    private String name;

    @NotBlank(message = "image is blank")
    private MultipartFile image;
}
