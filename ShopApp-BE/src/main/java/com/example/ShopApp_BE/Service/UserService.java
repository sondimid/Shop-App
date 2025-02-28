package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.Model.Entity.UserEntity;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    Optional<UserEntity> getUserByEmail(String email);
}
