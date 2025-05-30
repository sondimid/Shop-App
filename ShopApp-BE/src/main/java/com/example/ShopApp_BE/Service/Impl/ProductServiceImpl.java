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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final FileProperties fileProperties;
    private final ImageRepository imageRepository;
    @Override
    public ProductEntity createProduct(ProductDTO productDTO) throws NotFoundException, IOException {
        ProductEntity productEntity = modelMapper.map(productDTO, ProductEntity.class);
        productEntity.setFinalPrice(productEntity.getPrice() - productEntity.getDiscount() / 100.0 * productEntity.getPrice());
        CategoryEntity categoryEntity = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException(MessageKeys.CATEGORY_NOT_FOUND));
        productEntity.setCategoryEntity(categoryEntity);

        Path uploadPath = Paths.get(fileProperties.getDir());
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        List<ImageEntity> imageEntities = new ArrayList<>();
        if(productDTO.getImages() != null) {
            for(MultipartFile file : productDTO.getImages()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replace(" ","");
                file.transferTo(new File(fileProperties.getDir() + fileName));
                String url = fileProperties.getUrl() + fileName;
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
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND));
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

        Path uploadPath = Paths.get(fileProperties.getDir());
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        if(productUpdateDTO.getImages() != null) {
            for(MultipartFile file : productUpdateDTO.getImages()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replace(" ","");
                file.transferTo(new File(fileProperties.getDir() + fileName));
                String url = fileProperties.getUrl() + fileName;
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

}
