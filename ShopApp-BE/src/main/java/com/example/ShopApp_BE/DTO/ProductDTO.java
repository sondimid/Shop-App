package com.example.ShopApp_BE.Model.DTO;


import com.example.ShopApp_BE.DTO.ImageDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProductDTO {
    @NotBlank(message = "name of product is blank")
    private String name;

    @NotBlank(message = "price is blank")
    private Double price;

    @NotBlank(message = "image is blank")
    private List<ImageDTO> imageDTOs;

    @NotBlank(message = "description is blank")
    private String description;

    @NotBlank(message = "category is blank")
    private Long categoryId;

    private Double discount;
}
