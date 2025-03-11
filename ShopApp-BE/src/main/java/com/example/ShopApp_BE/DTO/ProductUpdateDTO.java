package com.example.ShopApp_BE.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Getter
@Setter
@Builder
public class ProductUpdateDTO {
    @NotBlank(message = "name of product is blank")
    private String name;

    @NotBlank(message = "price is blank")
    private Double price;

    @NotBlank(message = "description is blank")
    private String description;

    @NotBlank(message = "image is blank")
    private List<MultipartFile> images;

    @NotBlank(message = "category is blank")
    private Long categoryId;

    private Double discount;

    private List<Long> IdImageDelete;

}
