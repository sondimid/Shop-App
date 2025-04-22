package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.DTO.ProductDTO;
import com.example.ShopApp_BE.DTO.ProductUpdateDTO;
import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.Model.Response.PageResponse;
import com.example.ShopApp_BE.Model.Response.ProductResponse;
import com.example.ShopApp_BE.Service.ProductService;
import com.example.ShopApp_BE.Utils.MessageKeys;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor

public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(@ModelAttribute ProductDTO productDTO) throws IOException, NotFoundException {
        productService.createProduct(productDTO);
        return ResponseEntity.ok(MessageKeys.CREATE_PRODUCT_SUCCESS);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id) throws Exception {

        return ResponseEntity.ok().body(productService.getProduct(id));

    }

    @GetMapping("")
    public ResponseEntity<?> getProductsByCategory(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit,
            @RequestParam(name = "sort", defaultValue = "ASC") String sort,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "fromPrice", defaultValue = "0") Double fromPrice,
            @RequestParam(name = "toPrice", defaultValue = "2000") Double toPrice,
            @RequestParam(name = "categoryId", defaultValue = "1") Long categoryId){

        Sort.Direction sortDirection =
                sort.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, limit, sortDirection, sortField);
        Page<ProductResponse> productPage = productService.getByCategoryId(categoryId, keyword.trim(), fromPrice, toPrice, pageable);
        return ResponseEntity.ok().body(PageResponse.builder()
                .pageSize(limit)
                .pageNumber(page)
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .content(productPage.getContent())
                .build());

    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(@ModelAttribute ProductUpdateDTO productUpdateDTO) throws Exception {

        productService.updateProduct(productUpdateDTO);
        return ResponseEntity.ok().body(MessageKeys.UPDATE_SUCCESS);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) throws Exception {

        productService.deleteProductById(id);
        return ResponseEntity.ok().body(MessageKeys.DELETE_PRODUCT_SUCCESS);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/fake-data")
    public ResponseEntity<?> fakeData(@ModelAttribute List<MultipartFile> images) throws IOException, NotFoundException {
        List<MultipartFile> image = images;
        Faker faker = new Faker(new Locale("vi"));
        for(int i = 0; i < 100; i++){
            String name = faker.commerce().productName();
            if(productService.existByName(name)) continue;
            ProductDTO productDTO = ProductDTO.builder()
                    .name(name)
                    .description(faker.lorem().sentence())
                    .categoryId((long)faker.number().numberBetween(1,5))
                    .price(faker.number().randomDouble(1, 5, 100))
                    .discount(faker.number().randomDouble(0, 5, 50))
                    .images(List.of(image.get(i % images.size())))
                    .build();
            productService.createProduct(productDTO);
        }
        return ResponseEntity.ok().body(MessageKeys.FAKE_DATA_SUCCESS);

    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProduct(@RequestParam(name = "keyword", defaultValue = "") String keyword,
                                           @RequestParam(name = "page", defaultValue = "0") Integer page,
                                           @RequestParam(name = "limit", defaultValue = "10") Integer limit,
                                           @RequestParam(name = "sort", defaultValue = "ASC") String sort,
                                           @RequestParam(name = "sortField", defaultValue = "id") String sortField,
                                           @RequestParam(name = "fromPrice", defaultValue = "0") Double fromPrice,
                                           @RequestParam(name = "toPrice", defaultValue = "200000") Double toPrice){
        Sort.Direction sortDirection = sort.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, limit, sortDirection, sortField);
        Page<ProductResponse> productResponsePage = productService.getAll(keyword.trim(), fromPrice, toPrice, pageable);
        return ResponseEntity.ok().body(PageResponse.builder()
                .content(productResponsePage.getContent())
                .totalPages(productResponsePage.getTotalPages())
                .totalElements(productResponsePage.getTotalElements())
                .pageNumber(productResponsePage.getNumber())
                .pageSize(productResponsePage.getSize())
                .build());
    }



}
