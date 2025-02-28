package com.example.ShopApp_BE.Model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class ImageDTO {
    @NotNull(message = "url is null")
    private String url;

    @NotNull(message = "file is null")
    private MultipartFile file;
}
