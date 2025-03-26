package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.DTO.CategoryDTO;
import com.example.ShopApp_BE.Repository.CategoryRepository;
import com.example.ShopApp_BE.Service.CategoryService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("")
    public ResponseEntity<?> createCategory(@ModelAttribute CategoryDTO categoryDTO) throws IOException {

        categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(MessageKeys.CREATE_CATEGORY_SUCCESS);

    }

    @PutMapping("")
    public ResponseEntity<?> updateCategory(@ModelAttribute CategoryDTO categoryDTO,
                                            @PathVariable("id") Long id) throws Exception {

        categoryService.updateCategory(categoryDTO, id);
        return ResponseEntity.ok(MessageKeys.UPDATE_SUCCESS);

    }

    @GetMapping("")
    public ResponseEntity<?> getAllCategories() {

        return ResponseEntity.ok().body(categoryService.getAll());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) throws Exception {

        return ResponseEntity.ok(categoryService.deleteById(id));

    }
}
