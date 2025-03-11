package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.Model.Response.PageResponse;
import com.example.ShopApp_BE.Model.Response.UserResponse;
import com.example.ShopApp_BE.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "3") Integer limit) {
        try{
            PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
            Page<UserResponse> userPage = userService.getAllUsers(pageRequest);
            return ResponseEntity.ok().body(PageResponse.builder()
                    .content(Arrays.asList(userPage.getContent().toArray()))
                    .pageNumber(page)
                    .pageSize(limit)
                    .totalElements(userPage.getTotalElements())
                    .totalPages(userPage.getTotalPages())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(name = "id") Long id) {
        try{
            return ResponseEntity.ok().body(userService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/lock")
    public ResponseEntity<?> lockUserByIds(@RequestBody List<Long> ids) {
        try {
            return ResponseEntity.accepted().body(userService.lockByIds(ids));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/unlock")
    public ResponseEntity<?> unlockUserByIds(@RequestBody List<Long> ids) {
        try {
            return ResponseEntity.accepted().body(userService.unLockByIds(ids));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
