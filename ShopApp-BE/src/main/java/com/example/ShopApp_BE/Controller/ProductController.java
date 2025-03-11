package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.DTO.ProductDTO;
import com.example.ShopApp_BE.DTO.ProductUpdateDTO;
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
    public ResponseEntity<?> createProduct(@ModelAttribute ProductDTO productDTO) {
        try{
            productService.createProduct(productDTO);
            return ResponseEntity.ok(MessageKeys.CREATE_PRODUCT_SUCCESS);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id){
        try{
            return ResponseEntity.ok().body(productService.getProduct(id));
        } catch (Exception e) {
            return ResponseEntity.ok().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getProductsByCategory(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit,
            @RequestParam(name = "sort", defaultValue = "ASC") String sort,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "categoryId") Long categoryId){
        try{
            Sort.Direction sortDirection =
                    sort.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, limit, sortDirection, sortField);
            Page<ProductResponse> productPage = productService.getByCategoryId(categoryId, keyword, pageable);
            return ResponseEntity.ok().body(PageResponse.builder()
                    .pageSize(limit)
                    .pageNumber(page)
                    .totalElements(productPage.getTotalElements())
                    .totalPages(productPage.getTotalPages())
                    .content(Arrays.asList(productPage.getContent().toArray()))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(@ModelAttribute ProductUpdateDTO productUpdateDTO,
                                           @PathVariable("id") Long id) {
        try{
            productService.updateProduct(productUpdateDTO, id);
            return ResponseEntity.ok().body(MessageKeys.UPDATE_SUCCESS);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        try{
            productService.deleteProductById(id);
            return ResponseEntity.ok().body(MessageKeys.DELETE_PRODUCT_SUCCESS);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/fake-data")
    public ResponseEntity<?> fakeData(@RequestBody List<MultipartFile> images) {
        try {
            Faker faker = new Faker(new Locale("vi"));
            for(int i = 0; i < 10000; i++){
                String name = faker.commerce().productName();
                if(productService.existByName(name)) continue;
                ProductDTO productDTO = ProductDTO.builder()
                        .name(name)
                        .description(faker.lorem().sentence())
                        .categoryId((long)faker.number().numberBetween(1,5))
                        .price(faker.number().randomDouble(2, 5000000, 100000000))
                        .discount(faker.number().randomDouble(0, 5, 50))
                        .images(images)
                        .build();
                productService.createProduct(productDTO);
            }
            return ResponseEntity.ok().body(MessageKeys.FAKE_DATA_SUCCESS);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
