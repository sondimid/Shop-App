package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.DTO.CategoryDTO;
import com.example.ShopApp_BE.Repository.CategoryRepository;
import com.example.ShopApp_BE.Service.CategoryService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("")
    public ResponseEntity<?> createCategory(@ModelAttribute CategoryDTO categoryDTO) {
        try{
            categoryService.createCategory(categoryDTO);
            return ResponseEntity.ok(MessageKeys.CREATE_CATEGORY_SUCCESS);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
