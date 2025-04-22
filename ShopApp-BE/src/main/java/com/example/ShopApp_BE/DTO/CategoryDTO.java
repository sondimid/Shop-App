package com.example.ShopApp_BE.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "name is bank")
    @NotNull(message = "name is blank")
    private String name;

    private MultipartFile image;
}
