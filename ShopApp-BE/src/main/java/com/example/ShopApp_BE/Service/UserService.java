package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.DTO.*;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.TokenResponse;
import com.example.ShopApp_BE.Model.Response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    Page<UserResponse> getAllUsers(PageRequest pageRequest);

    UserResponse getById(Long id) throws Exception;

    String lockByIds(List<Long> ids);

    String unLockByIds(List<Long> ids);

    String uploadAvatar(String token, MultipartFile file) throws Exception;

    String forgotPassword(String email) throws Exception;

    UserEntity resetPassword(ResetPasswordDTO resetPasswordDTO) throws Exception;
}
