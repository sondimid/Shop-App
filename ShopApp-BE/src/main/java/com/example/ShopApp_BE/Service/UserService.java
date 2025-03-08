package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.DTO.UserChangePasswordDTO;
import com.example.ShopApp_BE.DTO.UserLoginDTO;
import com.example.ShopApp_BE.DTO.UserRegisterDTO;
import com.example.ShopApp_BE.DTO.UserUpdateDTO;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.TokenResponse;
import com.example.ShopApp_BE.Model.Response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserEntity> getUserByEmail(String email);

    UserEntity createUser(UserRegisterDTO userRegisterDTO);

    TokenResponse login(UserLoginDTO userLoginDTO);

    UserEntity update(UserUpdateDTO userUpdateDTO, String token);

    void changePassword(@Valid UserChangePasswordDTO userChangePasswordDTO, String token);

    UserResponse getUserDetails(String token) throws Exception;

    List<UserResponse> getAllUsers();

    UserResponse getById(Long id) throws Exception;

    String lockByIds(List<Long> ids);

    String unLockByIds(List<Long> ids);

    String uploadAvatar(String token, MultipartFile file) throws Exception;
}
