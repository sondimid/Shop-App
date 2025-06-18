package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.*;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.TokenResponse;
import com.example.ShopApp_BE.Model.Response.UserResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserEntity> getUserByEmail(String email);

    UserEntity createUser(UserRegisterDTO userRegisterDTO) throws Exception;

    TokenResponse login(UserLoginDTO userLoginDTO, HttpServletResponse response) throws Exception;

    TokenResponse adminLogin(UserLoginDTO userLoginDTO,HttpServletResponse response) throws Exception;

    UserResponse update(UserUpdateDTO userUpdateDTO);

    void changePassword(@Valid UserChangePasswordDTO userChangePasswordDTO, HttpServletRequest request, HttpServletResponse response) throws Exception;

    UserResponse getUserDetails() throws Exception;

    Page<UserResponse> getAllUsers(String keyword,PageRequest pageRequest);

    UserResponse getById(Long id) throws Exception;

    String lockByIds(List<Long> ids);

    String unLockByIds(List<Long> ids);

    String uploadAvatar(MultipartFile file) throws Exception;

    String forgotPassword(String email) throws Exception;

    UserEntity resetPassword(ResetPasswordDTO resetPasswordDTO) throws Exception;

    UserEntity verifyAccount(@Valid UserVerifyDTO userVerifyDTO) throws Exception;

    void logout(Cookie cookie, HttpServletResponse response) throws Exception;

    TokenResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
