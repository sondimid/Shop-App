package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.DTO.OrderDetailDTO;
import com.example.ShopApp_BE.DTO.ProductDTO;
import com.example.ShopApp_BE.DTO.ProductUpdateDTO;
import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.Model.Entity.*;
import com.example.ShopApp_BE.Model.Response.ProductResponse;
import com.example.ShopApp_BE.Repository.CategoryRepository;
import com.example.ShopApp_BE.Repository.ImageRepository;
import com.example.ShopApp_BE.Repository.ProductRepository;
import com.example.ShopApp_BE.Service.ProductService;
import com.example.ShopApp_BE.Utils.FileProperties;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.OrderStatus;
import com.example.ShopApp_BE.Utils.UploadImages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.ShopApp_BE.Utils.MessageKeys.PRODUCT_HASH;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final UploadImages uploadImages;
    private final RedisServiceImpl<String, Long, Object> redisService;


    @Override
    public ProductEntity createProduct(ProductDTO productDTO) throws NotFoundException, IOException {
        ProductEntity productEntity = modelMapper.map(productDTO, ProductEntity.class);
        productEntity.setFinalPrice(productEntity.getPrice() - productEntity.getDiscount() / 100.0 * productEntity.getPrice());
        CategoryEntity categoryEntity = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException(MessageKeys.CATEGORY_NOT_FOUND));
        productEntity.setCategoryEntity(categoryEntity);


        List<ImageEntity> imageEntities = new ArrayList<>();
        if(productDTO.getImages() != null) {
            for(MultipartFile file : productDTO.getImages()) {
                String url = uploadImages.uploadImage(file);
                ImageEntity imageEntity = ImageEntity.builder()
                        .url(url)
                        .productEntity(productEntity)
                        .build();
                imageEntities.add(imageEntity);
            }
            productEntity.setImageEntities(imageEntities);
        }
        return productRepository.save(productEntity);
    }

    @Override
    public ProductResponse getProduct(Long id) throws Exception {
        final String key = PRODUCT_HASH + id;
        if (redisService.hasKey(key)) {
            Object cached = redisService.get(key);
            ObjectMapper mapper = new ObjectMapper();
            ProductResponse productResponse = mapper.convertValue(cached, ProductResponse.class);

            redisService.setTimeToLive(key, 3 * 60 * 1000);
            return productResponse;
        }
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND));
        redisService.set(key, ProductResponse.fromProductEntity(productEntity));
        redisService.setTimeToLive(key, 3 * 60 * 1000);
        return ProductResponse.fromProductEntity(productEntity);
    }

    @Override
    public Page<ProductResponse> getByCategoryId(Long categoryId, String keyword, Double fromPrice, Double toPrice, Pageable pageable) {
        Page<ProductEntity> productEntities = productRepository.findByCategoryEntity_Id(categoryId, keyword, fromPrice, toPrice, pageable);
        return productEntities.map(ProductResponse::fromProductEntity);
    }

    @Override
    public ProductResponse updateProduct(ProductUpdateDTO productUpdateDTO) throws Exception {
         ProductEntity productEntity = (productUpdateDTO.getId() == null)
                ? ProductEntity.builder()
                 .cartDetailEntities(new ArrayList<>())
                 .commentEntities(new ArrayList<>())
                 .imageEntities(new ArrayList<>())
                 .orderDetailEntities(new ArrayList<>())
                 .build()
                : productRepository.findById(productUpdateDTO.getId())
                .orElseThrow(() -> new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND));
        productEntity.setName(productUpdateDTO.getName());
        productEntity.setDescription(productUpdateDTO.getDescription());
        productEntity.setPrice(productUpdateDTO.getPrice());
        productEntity.setDiscount(productUpdateDTO.getDiscount());
        productEntity.setQuantity(productUpdateDTO.getQuantity());

        productEntity.setFinalPrice(productEntity.getPrice() - productEntity.getDiscount() / 100.0 * productEntity.getPrice());
        productEntity.setCategoryEntity(categoryRepository.findById(productUpdateDTO.getCategoryId()).orElseThrow(
                () -> new NotFoundException(MessageKeys.CATEGORY_NOT_FOUND)));

        if(productUpdateDTO.getImages() != null) {
            for(MultipartFile file : productUpdateDTO.getImages()) {
                String url = uploadImages.uploadImage(file);
                ImageEntity imageEntity = ImageEntity.builder()
                        .url(url)
                        .productEntity(productEntity).build();
                productEntity.getImageEntities().add(imageEntity);
            }
        }
        if(productUpdateDTO.getIdImageDelete() != null) {
            for(Long imageId : productUpdateDTO.getIdImageDelete()){
                ImageEntity imageEntity = imageRepository.findById(imageId).orElseThrow(
                        () -> new NotFoundException(MessageKeys.IMAGE_NOT_FOUND)
                );
                imageRepository.delete(imageEntity);
            }
        }
        return ProductResponse.fromProductEntity(productRepository.save(productEntity));
    }

    @Override
    public void deleteProductById(Long id) throws Exception {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND));
        productRepository.delete(productEntity);
    }

    @Override
    public boolean existByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public Page<ProductResponse> getAll(String keyword, Double fromPrice, Double toPrice, Pageable pageable) {
        Page<ProductEntity> productEntityPage = productRepository.findByKeyword(keyword, fromPrice, toPrice, pageable);
        return productEntityPage.map(ProductResponse::fromProductEntity);
    }

    @Override
    public List<ProductResponse> getNewestProducts(Pageable pageable) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        if(redisService.hasKey(MessageKeys.NEW_HASH)) {
            String json = (String) redisService.get(MessageKeys.NEW_HASH);
            List<ProductResponse> productResponseList = mapper.readValue(json, new TypeReference<>() {});
            redisService.setTimeToLive(MessageKeys.NEW_HASH, 10 * 60 * 1000);
            return productResponseList;
        }
        List<ProductEntity> productEntityList = productRepository.findNewest(pageable);
        List<ProductResponse> productResponseList = productEntityList
                .stream()
                .map(ProductResponse::fromProductEntity)
                .toList();
        String json = mapper.writeValueAsString(productResponseList);
        redisService.set(MessageKeys.NEW_HASH, json);
        redisService.setTimeToLive(MessageKeys.NEW_HASH, 10 * 60 * 1000);
        return productResponseList;
    }

    @Override
    public List<ProductResponse> getBestDiscountProducts(Pageable pageable) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        if(redisService.hasKey(MessageKeys.DISCOUNT_HASH)){
            String json = (String) redisService.get(MessageKeys.DISCOUNT_HASH);
            List<ProductResponse> productResponseList = mapper.readValue(json, new TypeReference<>() {});
            redisService.setTimeToLive(MessageKeys.DISCOUNT_HASH, 10 * 60 * 1000);
            return productResponseList;
        }
        List<ProductEntity> productEntityList = productRepository.findNewest(pageable);
        List<ProductResponse> productResponseList = productEntityList
                .stream()
                .map(ProductResponse::fromProductEntity)
                .toList();
        String json = mapper.writeValueAsString(productResponseList);
        redisService.set(MessageKeys.DISCOUNT_HASH, json);
        redisService.setTimeToLive(MessageKeys.DISCOUNT_HASH, 10 * 60 * 1000);
        return productResponseList;
    }


}
