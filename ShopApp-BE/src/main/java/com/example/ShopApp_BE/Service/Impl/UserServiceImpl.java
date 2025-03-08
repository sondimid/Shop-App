package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.DTO.UserChangePasswordDTO;
import com.example.ShopApp_BE.DTO.UserLoginDTO;
import com.example.ShopApp_BE.DTO.UserRegisterDTO;
import com.example.ShopApp_BE.DTO.UserUpdateDTO;
import com.example.ShopApp_BE.Model.Entity.RoleEntity;
import com.example.ShopApp_BE.Model.Entity.TokenEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.TokenResponse;
import com.example.ShopApp_BE.Model.Response.UserResponse;
import com.example.ShopApp_BE.Repository.RoleRepository;
import com.example.ShopApp_BE.Repository.TokenRepository;
import com.example.ShopApp_BE.Repository.UserRepository;
import com.example.ShopApp_BE.Service.UserService;
import com.example.ShopApp_BE.Utils.ImageFileUtils;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String UPLOAD_AVATAR_DIR = "D:/uploads/ShopApp-BE/";
    private static final String FILE_URL = "http://localhost:8080/uploads/";

    @Override
    public Optional<UserEntity> getUserByEmail(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public UserEntity createUser(UserRegisterDTO userRegisterDTO) {
        if(userRepository.existsByPhoneNumber(userRegisterDTO.getPhoneNumber())){
            throw new DataIntegrityViolationException(MessageKeys.PHONENUMBER_EXISTED);
        }

        if(!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())){
            throw new DataIntegrityViolationException(MessageKeys.PASSWORD_NOT_MATCH);
        }

        UserEntity userEntity = modelMapper.map(userRegisterDTO, UserEntity.class);
        RoleEntity roleEntity = roleRepository.findById(2L).get();
        userEntity.setRoleEntity(roleEntity);
        userEntity.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));

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

        TokenEntity tokenEntity = TokenEntity.builder()
                .accessToken(jwtTokenUtils.generateToken(userEntity, ACCESS_TOKEN))
                .refreshToken(jwtTokenUtils.generateToken(userEntity, REFRESH_TOKEN))
                .userEntity(userEntity)
                .revoked(Boolean.FALSE)
                .build();
        tokenRepository.save(tokenEntity);
        return TokenResponse.fromTokenEntity(tokenEntity);
    }

    @Override
    public UserEntity update(UserUpdateDTO userUpdateDTO, String token) {
        UserEntity userEntity = userRepository.findByPhoneNumber(jwtTokenUtils.extractPhoneNumber(token, ACCESS_TOKEN)).get();

        if(userRepository.existsByEmail(userUpdateDTO.getEmail())){
            throw new DataIntegrityViolationException(MessageKeys.EMAIL_EXISTED);
        }
        modelMapper.map(userUpdateDTO, userEntity);
        return userRepository.save(userEntity);
    }

    @Override
    public void changePassword(UserChangePasswordDTO userChangePasswordDTO, String token) {
        UserEntity userEntity = userRepository.findByPhoneNumber(jwtTokenUtils.extractPhoneNumber(token, ACCESS_TOKEN)).get();

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
                .findByPhoneNumber(jwtTokenUtils.extractPhoneNumber(token, ACCESS_TOKEN))
                .orElseThrow(() -> new Exception(MessageKeys.ACCESS_TOKEN_INVALID));
        return UserResponse.fromUserEntity(userEntity);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return UserResponse.fromUserEntities(userEntities);
    }

    @Override
    public UserResponse getById(Long id) throws Exception {
        UserEntity userEntity = userRepository
                .findById(id)
                .orElseThrow(() -> new Exception(MessageKeys.USER_ID_NOT_FOUND));
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
                .findByPhoneNumber(jwtTokenUtils.extractPhoneNumber(token, ACCESS_TOKEN))
                .orElseThrow(() -> new Exception(MessageKeys.ACCESS_TOKEN_INVALID));
        Path uploadPath = Paths.get(UPLOAD_AVATAR_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        // save file to Drive D
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replace(" ","");
        file.transferTo(new File(UPLOAD_AVATAR_DIR + fileName));

        String url = FILE_URL + fileName;
        userEntity.setAvatarUrl(url);
        userRepository.save(userEntity);
        return url;
    }
}
