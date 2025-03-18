package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.DTO.ProductDTO;
import com.example.ShopApp_BE.DTO.ProductUpdateDTO;
import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import com.example.ShopApp_BE.Model.Response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface ProductService {
    ProductEntity createProduct(ProductDTO productDTO) throws IOException, NotFoundException;

    ProductResponse getProduct(Long id) throws Exception;

    Page<ProductResponse> getByCategoryId(Long categoryId, String keyword, Pageable pageable);

    ProductResponse updateProduct(ProductUpdateDTO productUpdateDTO, Long id) throws Exception;

    void deleteProductById(Long id) throws Exception;

    boolean existByName(String name);
}
