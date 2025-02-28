package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Repository.UserRepository;
import com.example.ShopApp_BE.Service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Optional<UserEntity> getUserByEmail(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
}
