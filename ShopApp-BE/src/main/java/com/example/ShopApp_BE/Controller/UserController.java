package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.Model.DTO.UserLoginDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) {
        System.out.println("login");
        return ResponseEntity.accepted().body("okg");
    }
}
