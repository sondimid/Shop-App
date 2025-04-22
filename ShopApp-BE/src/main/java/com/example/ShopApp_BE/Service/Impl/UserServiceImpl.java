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
import java.nio.channels.NotYetBoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
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
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity createUser(UserRegisterDTO userRegisterDTO) throws Exception {
        if(!roleRepository.existsById(2L)){
            RoleEntity roleAdmin = RoleEntity.builder()
                    .role("ADMIN")
                    .build();
            roleRepository.save(roleAdmin);
            RoleEntity roleUser = RoleEntity.builder()
                    .role("USER")
                    .build();
            roleRepository.save(roleUser);
        }
        if(!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())){
            throw new DataIntegrityViolationException(MessageKeys.PASSWORD_NOT_MATCH);
        }
        Optional<UserEntity> userOptional = userRepository.findByEmail(userRegisterDTO.getEmail());
        SecureRandom random = new SecureRandom();
        String otp = String.format("%06d", random.nextInt(1000000));
        LocalDateTime otpExp = LocalDateTime.now().plusMinutes(6L);
        if(userOptional.isPresent()) {
            if(userOptional.get().getIsActive()) throw new DataIntegrityViolationException(MessageKeys.EMAIL_EXISTED);
            UserEntity userEntity = userOptional.get();
            userEntity.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
            userEntity.setFullName(userRegisterDTO.getFullName());
            userEntity.setEmail(userRegisterDTO.getEmail());
            userEntity.setOtp(passwordEncoder.encode(otp));
            userEntity.setOtpExpiration(otpExp);
            mailService.sendEmailOtp(userEntity, otp);
            return userRepository.save(userEntity);
        }

        UserEntity userEntity = modelMapper.map(userRegisterDTO, UserEntity.class);
        userEntity.setRoleEntity(roleRepository.findById(2L).get());
        userEntity.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        CartEntity cartEntity = CartEntity.builder()
                .userEntity(userEntity)
                .cartDetailEntities(new ArrayList<>())
                .build();
        userEntity.setCartEntity(cartEntity);
        userEntity.setOtp(passwordEncoder.encode(otp));
        userEntity.setOtpExpiration(otpExp);
        userEntity.setIsActive(Boolean.FALSE);
        mailService.sendEmailOtp(userEntity, otp);
        return userRepository.save(userEntity);
    }

    @Override
    public TokenResponse login(UserLoginDTO userLoginDTO) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(userLoginDTO.getEmail());
        if(userOptional.isEmpty()){
            throw new DataIntegrityViolationException(MessageKeys.LOGIN_FAILED);
        }
        UserEntity userEntity = userOptional.get();
        if(!passwordEncoder.matches(userLoginDTO.getPassword(), userEntity.getPassword())){
            throw new DataIntegrityViolationException(MessageKeys.LOGIN_FAILED);
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
    public UserResponse update(UserUpdateDTO userUpdateDTO, String token) {
        UserEntity userEntity = userRepository.findByEmail(jwtTokenUtils.extractEmail(token, TokenType.ACCESS)).get();

        modelMapper.map(userUpdateDTO, userEntity);
        return UserResponse.fromUserEntity(userRepository.save(userEntity));
    }

    @Override
    public void changePassword(UserChangePasswordDTO userChangePasswordDTO, String token) {
        UserEntity userEntity = userRepository.findByEmail(jwtTokenUtils.extractEmail(token, TokenType.ACCESS)).get();

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
                .findById(jwtTokenUtils.extractId(token, TokenType.ACCESS))
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        return UserResponse.fromUserEntity(userEntity);
    }

    @Override
    public Page<UserResponse> getAllUsers(String keyword, PageRequest pageRequest) {
        Page<UserEntity> userPage = userRepository.findByKeyword(keyword,pageRequest);
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
                .findByEmail(jwtTokenUtils.extractEmail(token, TokenType.ACCESS))
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
        String confirmUrl = "http://localhost:3000/reset-password?token=" + resetToken;
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

    @Override
    public UserEntity verifyAccount(UserVerifyDTO userVerifyDTO) throws Exception {
        UserEntity userEntity = userRepository.findByEmail(userVerifyDTO.getEmail())
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        if(userEntity.getEmail().equals(userVerifyDTO.getEmail())){
                if(userEntity.getOtpExpiration().isAfter(LocalDateTime.now())){
                if(passwordEncoder.matches(userVerifyDTO.getOtp(), userEntity.getOtp())){
                    userEntity.setIsActive(Boolean.TRUE);
                    userEntity.setOtpExpiration(LocalDateTime.now());
                    return userRepository.save(userEntity);
                }
                throw new Exception(MessageKeys.OTP_NOT_MATCH);
            }
            throw new Exception(MessageKeys.OTP_EXPIRED);
        }
        throw new Exception(MessageKeys.EMAIL_NOT_MATCH);
    }
}
