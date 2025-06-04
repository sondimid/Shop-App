package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.UnauthorizedAccessException;
import com.example.ShopApp_BE.DTO.*;
import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.Model.Entity.CartEntity;
import com.example.ShopApp_BE.Model.Entity.RoleEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.TokenResponse;
import com.example.ShopApp_BE.Model.Response.UserResponse;
import com.example.ShopApp_BE.Repository.CartRepository;
import com.example.ShopApp_BE.Repository.RoleRepository;
import com.example.ShopApp_BE.Repository.UserRepository;
import com.example.ShopApp_BE.Service.MailService;
import com.example.ShopApp_BE.Service.RedisService;
import com.example.ShopApp_BE.Service.UserService;
import com.example.ShopApp_BE.Utils.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final MailService mailService;
    private final UploadImages uploadImages;
    private final CookieUtils cookieUtils;
    private final RedisService<String, String, String> redisService;


    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity createUser(UserRegisterDTO userRegisterDTO) throws Exception {
        if(!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())){
            throw new DataIntegrityViolationException(MessageKeys.PASSWORD_NOT_MATCH);
        }
        Optional<UserEntity> userOptional = userRepository.findByEmail(userRegisterDTO.getEmail());
        SecureRandom random = new SecureRandom();
        String otp = String.format("%06d", random.nextInt(1000000));
        if(userOptional.isPresent()) {
            if(userOptional.get().getIsActive().equals(Boolean.TRUE))
                throw new DataIntegrityViolationException(MessageKeys.EMAIL_EXISTED);
            UserEntity userEntity = userOptional.get();
            userEntity.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
            userEntity.setFullName(userRegisterDTO.getFullName());
            userEntity.setEmail(userRegisterDTO.getEmail());
            redisService.set(MessageKeys.OTP_HASH + userEntity.getEmail(), otp);
            redisService.setTimeToLive(MessageKeys.OTP_HASH + userEntity.getEmail(), 5 * 60 * 1000);
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
        redisService.set(MessageKeys.OTP_HASH + userEntity.getEmail(), otp);
        redisService.setTimeToLive(MessageKeys.OTP_HASH + userEntity.getEmail(), 5 * 60 * 1000);
        userEntity.setIsActive(Boolean.FALSE);
        mailService.sendEmailOtp(userEntity, otp);
        return userRepository.save(userEntity);
    }

    @Override
    public TokenResponse login(UserLoginDTO userLoginDTO, HttpServletResponse response) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(userLoginDTO.getEmail());
        if(userOptional.isEmpty()){
            throw new DataIntegrityViolationException(MessageKeys.LOGIN_FAILED);
        }
        UserEntity userEntity = userOptional.get();
        if(!passwordEncoder.matches(userLoginDTO.getPassword(), userEntity.getPassword())){
            throw new DataIntegrityViolationException(MessageKeys.LOGIN_FAILED);
        }
        String accessToken = jwtTokenUtils.generateToken(userEntity, TokenType.ACCESS);
        String refreshToken = jwtTokenUtils.generateToken(userEntity, TokenType.REFRESH);
        Cookie cookieRefresh = cookieUtils.getCookie(refreshToken);
        response.addCookie(cookieRefresh);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public TokenResponse adminLogin(UserLoginDTO userLoginDTO, HttpServletResponse response) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(userLoginDTO.getEmail());
        if(userOptional.isEmpty()){
            throw new DataIntegrityViolationException(MessageKeys.LOGIN_FAILED);
        }
        UserEntity userEntity = userOptional.get();
        if(!passwordEncoder.matches(userLoginDTO.getPassword(), userEntity.getPassword())){
            throw new DataIntegrityViolationException(MessageKeys.LOGIN_FAILED);
        }
        if(!userEntity.getRoleEntity().getRole().equals(MessageKeys.ROLE_ADMIN)){
            throw new UnauthorizedAccessException(MessageKeys.UNAUTHORIZED);
        }
        String accessToken = jwtTokenUtils.generateToken(userEntity, TokenType.ACCESS);
        String refreshToken = jwtTokenUtils.generateToken(userEntity, TokenType.REFRESH);
        Cookie cookieRefresh = cookieUtils.getCookie(refreshToken);
        response.addCookie(cookieRefresh);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public UserResponse update(UserUpdateDTO userUpdateDTO, String token) {
        UserEntity userEntity = userRepository.findByEmail(jwtTokenUtils.extractEmail(token, TokenType.ACCESS)).get();

        modelMapper.map(userUpdateDTO, userEntity);
        return UserResponse.fromUserEntity(userRepository.save(userEntity));
    }

    @Override
    public void changePassword(UserChangePasswordDTO userChangePasswordDTO, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader("Authorization").substring(7);
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(MessageKeys.REFRESH_TOKEN_HEADER))
                .findFirst().get().getValue();
        UserEntity userEntity = userRepository.findByEmail(jwtTokenUtils.extractEmail(accessToken, TokenType.ACCESS)).get();

        if(!passwordEncoder.matches(userChangePasswordDTO.getCurrentPassword(), userEntity.getPassword())){
            throw new DataIntegrityViolationException(MessageKeys.PASSWORD_NOT_MATCH);
        }
        if(!userChangePasswordDTO.getNewPassword().equals(userChangePasswordDTO.getConfirmPassword())){
            throw new DataIntegrityViolationException(MessageKeys.CONFIRM_PASSWORD_NOT_MATCH);
        }
        userEntity.setTokenVersion(userEntity.getTokenVersion() + 1);
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
        String avatarUrl = uploadImages.uploadImage(file);
        userEntity.setAvatarUrl(avatarUrl);
        userRepository.save(userEntity);
        return avatarUrl;
    }

    @Override
    public String forgotPassword(String email) throws Exception {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        if(userEntity.getIsActive().equals(Boolean.FALSE)){
            throw new UnauthorizedAccessException(MessageKeys.ACCOUNT_LOCK);
        }
        String resetToken = UUID.randomUUID().toString();
        redisService.set(MessageKeys.OTP_HASH + resetToken, email);
        redisService.setTimeToLive(MessageKeys.OTP_HASH + resetToken, 5 * 60 * 1000);
        String confirmUrl = "http://localhost:3000/reset-password?token=" + resetToken;
        mailService.sendEmailResetPassword(confirmUrl, userRepository.save(userEntity));
        return MessageKeys.SEND_EMAIL_RESET_PASSWORD_SUCCESS;
    }

    @Override
    public UserEntity resetPassword(ResetPasswordDTO resetPasswordDTO) throws Exception {
        if(!jwtTokenUtils.isNotExpired(resetPasswordDTO.getResetToken(), TokenType.RESET)){
            throw new UnauthorizedAccessException(MessageKeys.RESET_TOKEN_INVALID);
        }
        if(redisService.hasKey(MessageKeys.OTP_HASH + resetPasswordDTO.getResetToken())){
            String email  = redisService.get(MessageKeys.OTP_HASH + resetPasswordDTO.getResetToken());
            UserEntity userEntity = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
            if(userEntity.getIsActive().equals(Boolean.FALSE)) throw new Exception(MessageKeys.ACCOUNT_LOCK);
            if(!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword()))
                throw new Exception(MessageKeys.CONFIRM_PASSWORD_NOT_MATCH);
            userEntity.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
            redisService.del(MessageKeys.OTP_HASH + resetPasswordDTO.getResetToken());
            userEntity.setTokenVersion(userEntity.getTokenVersion() + 1);
            return userRepository.save(userEntity);
        }
        throw new NotFoundException(MessageKeys.OTP_EXPIRED);
    }

    @Override
    public UserEntity verifyAccount(UserVerifyDTO userVerifyDTO) throws Exception {
        UserEntity userEntity = userRepository.findByEmail(userVerifyDTO.getEmail())
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        if(userEntity.getIsActive().equals(Boolean.TRUE))
            throw new UnauthorizedAccessException(MessageKeys.UNAUTHORIZED);
        if(redisService.hasKey(MessageKeys.OTP_HASH + userVerifyDTO.getEmail())){
            if(redisService.get(MessageKeys.OTP_HASH + userVerifyDTO.getEmail()).equals(userVerifyDTO.getOtp())){
                userEntity.setIsActive(Boolean.TRUE);
                userEntity.setTokenVersion(1L);
                redisService.del(MessageKeys.OTP_HASH + userVerifyDTO.getEmail());
                return userRepository.save(userEntity);
            }
            throw new Exception(MessageKeys.OTP_EXPIRED);
        }
        throw new NotFoundException(MessageKeys.OTP_EXPIRED);
    }

    @Override
    public void logout(String accessToken, Cookie cookie, HttpServletResponse response) {
        redisService.set(accessToken, MessageKeys.BLACKLIST_HASH);
        redisService.setTimeToLive(accessToken,
                jwtTokenUtils.extractExpiration(accessToken, TokenType.ACCESS) - System.currentTimeMillis());

        String refreshToken = cookie.getValue();
        redisService.set(refreshToken, MessageKeys.BLACKLIST_HASH);
        redisService.setTimeToLive(refreshToken,
                jwtTokenUtils.extractExpiration(refreshToken, TokenType.REFRESH) - System.currentTimeMillis());

        cookieUtils.invalidateCookie(cookie, response);
    }

    @Override
    public TokenResponse refreshToken(HttpServletRequest request, HttpServletResponse response)  {
        Cookie cookie = Arrays.stream(request.getCookies()).filter(cookie1 -> cookie1.getName()
                        .equals(MessageKeys.REFRESH_TOKEN_HEADER))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedAccessException(MessageKeys.UNAUTHORIZED));
        String refreshToken = cookie.getValue();
        UserEntity userEntity = userRepository
                .findByEmail(jwtTokenUtils.extractEmail(refreshToken, TokenType.REFRESH))
                .orElseThrow(() -> new UnauthorizedAccessException(MessageKeys.UNAUTHORIZED));

        if(jwtTokenUtils.isNotExpired(refreshToken, TokenType.REFRESH)) {
            String newAccessToken = jwtTokenUtils.generateToken(userEntity, TokenType.ACCESS);
            return new TokenResponse(newAccessToken);
        }

        redisService.set(refreshToken, MessageKeys.BLACKLIST_HASH);
        redisService.setTimeToLive(refreshToken,
                jwtTokenUtils.extractExpiration(refreshToken, TokenType.REFRESH) - System.currentTimeMillis());

        cookieUtils.invalidateCookie(cookie, response);
        throw new UnauthorizedAccessException(MessageKeys.UNAUTHORIZED);
    }
}
