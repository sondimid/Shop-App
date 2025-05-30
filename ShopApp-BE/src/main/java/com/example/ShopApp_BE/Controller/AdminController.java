package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.DTO.UserLoginDTO;
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
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "6") Integer limit,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Sort.Direction sort = sortDirection.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;

        PageRequest pageRequest = PageRequest.of(page, limit, sort, sortField);
        Page<UserResponse> userPage = userService.getAllUsers(keyword, pageRequest);
        return ResponseEntity.ok().body(PageResponse.builder()
                    .content(userPage.getContent())
                    .pageNumber(page)
                    .pageSize(limit)
                    .totalElements(userPage.getTotalElements())
                    .totalPages(userPage.getTotalPages())
                    .build());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(name = "id") Long id) throws Exception {

        return ResponseEntity.ok().body(userService.getById(id));

    }

    @PutMapping("/users/lock")
    public ResponseEntity<?> lockUserByIds(@RequestBody List<Long> ids) {

        return ResponseEntity.accepted().body(userService.lockByIds(ids));

    }

    @PutMapping("/users/unlock")
    public ResponseEntity<?> unlockUserByIds(@RequestBody List<Long> ids) {

        return ResponseEntity.accepted().body(userService.unLockByIds(ids));

    }

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody UserLoginDTO userLoginDTO){
        return ResponseEntity.accepted().body(userService.adminLogin(userLoginDTO));
    }
}
