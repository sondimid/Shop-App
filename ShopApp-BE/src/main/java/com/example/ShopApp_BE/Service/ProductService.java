package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.DTO.ProductDTO;
import com.example.ShopApp_BE.DTO.ProductUpdateDTO;
import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import com.example.ShopApp_BE.Model.Response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductEntity createProduct(ProductDTO productDTO) throws IOException;

    ProductResponse getProduct(Long id) throws Exception;

    Page<ProductResponse> getByCategoryId(Long categoryId, Pageable pageable);

    ProductResponse updateProduct(ProductUpdateDTO productUpdateDTO, Long id) throws Exception;

    void deleteProductById(Long id) throws Exception;
}
