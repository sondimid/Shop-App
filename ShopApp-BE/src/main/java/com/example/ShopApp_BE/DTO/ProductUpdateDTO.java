package com.example.ShopApp_BE.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Getter
@Setter
@Builder
public class ProductUpdateDTO {
    private Long id;

    @NotBlank(message = "name of product is blank")
    private String name;

    @NotBlank(message = "price is blank")
    private Double price;

    @NotBlank(message = "quantity is blank")
    private Long quantity;

    @NotBlank(message = "description is blank")
    private String description;

    @NotBlank(message = "image is blank")
    @NotNull(message = "image is blank")
    private List<MultipartFile> images;

    @NotBlank(message = "category is blank")
    private Long categoryId;

    private Double discount;

    private List<Long> IdImageDelete;

}
