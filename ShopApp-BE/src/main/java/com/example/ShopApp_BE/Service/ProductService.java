package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.DTO.ProductDTO;
import com.example.ShopApp_BE.DTO.ProductUpdateDTO;
import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import com.example.ShopApp_BE.Model.Response.ProductResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductEntity createProduct(ProductDTO productDTO) throws IOException, NotFoundException;

    ProductResponse getProduct(Long id) throws Exception;

    Page<ProductResponse> getByCategoryId(Long categoryId, String keyword, Double fromPrice, Double toPrice, Pageable pageable);

    ProductResponse updateProduct(ProductUpdateDTO productUpdateDTO) throws Exception;

    void deleteProductById(Long id) throws Exception;

    boolean existByName(String name);

    Page<ProductResponse> getAll(String keyword, Double fromPrice, Double toPrice, Pageable pageable);

    List<ProductResponse> getNewestProducts(Pageable pageable) throws JsonProcessingException;

    List<ProductResponse> getBestDiscountProducts(Pageable pageable) throws JsonProcessingException;
}
