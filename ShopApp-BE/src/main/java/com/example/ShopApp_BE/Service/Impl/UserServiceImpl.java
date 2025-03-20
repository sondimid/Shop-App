package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.DTO.*;
import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.Model.Entity.CartEntity;
import com.example.ShopApp_BE.Model.Entity.RoleEntity;
import com.example.ShopApp_BE.Model.Entity.TokenEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.TokenResponse;
import com.example.ShopApp_BE.Model.Response.UserResponse;
import com.example.ShopApp_BE.Repository.CartRepository;
import com.example.ShopApp_BE.Repository.RoleRepository;
import com.example.ShopApp_BE.Repository.TokenRepository;
import com.example.ShopApp_BE.Repository.UserRepository;
import com.example.ShopApp_BE.Service.MailService;
import com.example.ShopApp_BE.Service.UserService;
import com.example.ShopApp_BE.Utils.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final FileProperties fileProperties;
    private final MailService mailService;


    @Override
    public Optional<UserEntity> getUserByEmail(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public UserEntity createUser(UserRegisterDTO userRegisterDTO) {
        if(userRepository.existsByPhoneNumber(userRegisterDTO.getPhoneNumber())){
            throw new DataIntegrityViolationException(MessageKeys.PHONENUMBER_EXISTED);
        }

        if(userRepository.existsByEmail(userRegisterDTO.getEmail())){
            throw new DataIntegrityViolationException(MessageKeys.EMAIL_EXISTED);
        }
        if(!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())){
            throw new DataIntegrityViolationException(MessageKeys.PASSWORD_NOT_MATCH);
        }
        UserEntity userEntity = modelMapper.map(userRegisterDTO, UserEntity.class);
        RoleEntity roleEntity = roleRepository.findById(2L).get();
        userEntity.setRoleEntity(roleEntity);
        userEntity.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        CartEntity cartEntity = CartEntity.builder()
                .userEntity(userEntity)
                .cartDetailEntities(new ArrayList<>())
                .build();
        userEntity.setCartEntity(cartEntity);
        return userRepository.save(userEntity);
    }

    @Override
    public TokenResponse login(UserLoginDTO userLoginDTO) {
        Optional<UserEntity> userOptional = userRepository.findByPhoneNumber(userLoginDTO.getPhoneNumber());
        if(userOptional.isEmpty()){
            throw new DataIntegrityViolationException(MessageKeys.LOGIN_FAILED);
        }
        UserEntity userEntity = userOptional.get();
        if(!passwordEncoder.matches(userLoginDTO.getPassword(), userEntity.getPassword())){
            throw new DataIntegrityViolationException(MessageKeys.LOGIN_FAILED);
        }
        if(userEntity.getIsActive().equals(Boolean.FALSE)){
            throw new DataIntegrityViolationException(MessageKeys.ACCOUNT_LOCK);
        }
        String accessToken = jwtTokenUtils.generateToken(userEntity, TokenType.ACCESS);
        TokenEntity tokenEntity = TokenEntity.builder()
                .refreshToken(jwtTokenUtils.generateToken(userEntity, TokenType.REFRESH))
                .userEntity(userEntity)
                .revoked(Boolean.FALSE)
                .build();
        tokenRepository.save(tokenEntity);
        return TokenResponse.fromTokenEntity(tokenEntity, accessToken);
    }

    @Override
    public UserEntity update(UserUpdateDTO userUpdateDTO, String token) {
        UserEntity userEntity = userRepository.findByPhoneNumber(jwtTokenUtils.extractPhoneNumber(token, TokenType.ACCESS)).get();

        if(userRepository.existsByEmail(userUpdateDTO.getEmail())){
            throw new DataIntegrityViolationException(MessageKeys.EMAIL_EXISTED);
        }
        modelMapper.map(userUpdateDTO, userEntity);
        return userRepository.save(userEntity);
    }

    @Override
    public void changePassword(UserChangePasswordDTO userChangePasswordDTO, String token) {
        UserEntity userEntity = userRepository.findByPhoneNumber(jwtTokenUtils.extractPhoneNumber(token, TokenType.ACCESS)).get();

        if(!passwordEncoder.matches(userChangePasswordDTO.getCurrentPassword(), userEntity.getPassword())){
            throw new DataIntegrityViolationException(MessageKeys.PASSWORD_NOT_MATCH);
        }

        if(!userChangePasswordDTO.getNewPassword().equals(userChangePasswordDTO.getConfirmPassword())){
            throw new DataIntegrityViolationException(MessageKeys.CONFIRM_PASSWORD_NOT_MATCH);
        }

        userEntity.setPassword(passwordEncoder.encode(userChangePasswordDTO.getNewPassword()));
        userRepository.save(userEntity);
    }

    @Override
    public UserResponse getUserDetails(String token) throws Exception {
        UserEntity userEntity = userRepository
                .findByPhoneNumber(jwtTokenUtils.extractPhoneNumber(token, TokenType.ACCESS))
                .orElseThrow(() -> new Exception(MessageKeys.ACCESS_TOKEN_INVALID));
        return UserResponse.fromUserEntity(userEntity);
    }

    @Override
    public Page<UserResponse> getAllUsers(PageRequest pageRequest) {
        Page<UserEntity> userPage = userRepository.findAll(pageRequest);
        return userPage.map(UserResponse::fromUserEntity);
    }

    @Override
    public UserResponse getById(Long id) throws Exception {
        UserEntity userEntity = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        return UserResponse.fromUserEntity(userEntity);
    }

    @Override
    public String lockByIds(List<Long> ids) {
        List<UserEntity> userEntities = userRepository
                .findByIdIn(ids);
        userEntities.forEach(u -> u.setIsActive(Boolean.FALSE));
        return MessageKeys.UPDATE_SUCCESS;
    }

    @Override
    public String unLockByIds(List<Long> ids) {
        List<UserEntity> userEntities = userRepository
                .findByIdIn(ids);
        userEntities.forEach(u -> u.setIsActive(Boolean.TRUE));
        return MessageKeys.UPDATE_SUCCESS;
    }

    @Override
    public String uploadAvatar(String token, MultipartFile file) throws Exception {
        if(!ImageFileUtils.isImageFile(file)){
            throw new Exception(MessageKeys.IMAGE_NOT_VALID);
        }
        UserEntity userEntity = userRepository
                .findByPhoneNumber(jwtTokenUtils.extractPhoneNumber(token, TokenType.ACCESS))
                .orElseThrow(() -> new Exception(MessageKeys.ACCESS_TOKEN_INVALID));
        Path uploadPath = Paths.get(fileProperties.getDir());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        // save file to Drive D
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replace(" ","");
        file.transferTo(new File(fileProperties.getDir() + fileName));

        String url = fileProperties.getUrl() + fileName;
        userEntity.setAvatarUrl(url);
        userRepository.save(userEntity);
        return url;
    }

    @Override
    public String forgotPassword(String email) throws Exception {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        if(userEntity.getIsActive().equals(Boolean.FALSE)){
            throw new Exception(MessageKeys.ACCOUNT_LOCK);
        }
        String resetToken = jwtTokenUtils.generateToken(userEntity, TokenType.RESET);
        userEntity.setResetToken(resetToken);
        String confirmUrl = "http://localhost:8080/api/v1/users/reset-password?token=" + resetToken;
        mailService.sendEmailResetPassword(confirmUrl, userRepository.save(userEntity));
        return MessageKeys.SEND_EMAIL_RESET_PASSWORD_SUCCESS;
    }

    @Override
    public UserEntity resetPassword(ResetPasswordDTO resetPasswordDTO) throws Exception {
        if(!jwtTokenUtils.isNotExpired(resetPasswordDTO.getResetToken(), TokenType.RESET)){
            throw new Exception(MessageKeys.RESET_TOKEN_INVALID);
        }
        UserEntity userEntity = userRepository.findByResetToken(resetPasswordDTO.getResetToken())
                .orElseThrow(() -> new Exception(MessageKeys.USER_ID_NOT_FOUND));
        if(userEntity.getIsActive().equals(Boolean.FALSE)) throw new Exception(MessageKeys.ACCOUNT_LOCK);
        if(!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword()))
            throw new Exception(MessageKeys.CONFIRM_PASSWORD_NOT_MATCH);
        userEntity.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userEntity.setResetToken(null);
        userEntity.setTokenEntities(null);
        return userRepository.save(userEntity);
    }
}
