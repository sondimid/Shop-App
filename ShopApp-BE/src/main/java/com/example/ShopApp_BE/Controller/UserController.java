package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.UnauthorizedAccessException;
import com.example.ShopApp_BE.DTO.*;
import com.example.ShopApp_BE.Service.OAuth2Service;
import com.example.ShopApp_BE.Service.RedisService;
import com.example.ShopApp_BE.Service.UserService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.OAuth2Provider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OAuth2Service oAuth2Service;

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) throws Exception {
        return ResponseEntity.accepted().body(userService.login(userLoginDTO, response));

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) throws Exception {

        userService.createUser(userRegisterDTO);
        return ResponseEntity.accepted().body(MessageKeys.EMAIL_SEND_SUCCESS);

    }

    @PostMapping("/verify-account")
    public ResponseEntity<?> verifyAccount(@Valid @RequestBody UserVerifyDTO userVerifyDTO) throws Exception {
        userService.verifyAccount(userVerifyDTO);
        return ResponseEntity.accepted().body(MessageKeys.REGISTER_SUCCESS);
    }

    @PutMapping("/profiles")
    public ResponseEntity<?> update(@Valid @RequestBody UserUpdateDTO userUpdateDTO,
                                    @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) {
        String token = authorization.substring(7);

        return ResponseEntity.accepted().body(userService.update(userUpdateDTO, token));

    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO,
                                            HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {

        userService.changePassword(userChangePasswordDTO, request, response);
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken( HttpServletRequest request, HttpServletResponse response) throws Exception {
        return ResponseEntity.ok().body(userService.refreshToken(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String accessToken = request.getHeader(MessageKeys.AUTHORIZATION_HEADER).substring(7);
        Cookie cookie = Arrays.stream(request.getCookies())
                .filter(cookie1 -> cookie1.getName().equals(MessageKeys.REFRESH_TOKEN_HEADER))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedAccessException(MessageKeys.UNAUTHORIZED));
        userService.logout(accessToken, cookie, response);
        return ResponseEntity.accepted().body(MessageKeys.DELETE_TOKEN_SUCCESS);

    }

    @GetMapping("/profiles")
    public ResponseEntity<?> getUserDetails(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) throws Exception {
        String token = authorization.substring(7);
        return ResponseEntity.ok().body(userService.getUserDetails(token));

    }

    @PutMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization,
                                        @RequestBody MultipartFile file) throws Exception {

        return ResponseEntity.accepted()
                .body(userService.uploadAvatar(authorization.substring(7), file));

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody EmailDTO emailDTO) throws Exception {
        return ResponseEntity.accepted().body(userService.forgotPassword(emailDTO.getEmail()));

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) throws Exception {
        userService.resetPassword(resetPasswordDTO);
        return ResponseEntity.accepted().body(MessageKeys.RESET_PASSWORD_SUCCESS);
    }

    @GetMapping("/oauth2/{provider}")
    public ResponseEntity<?> socialLogin(@RequestParam("code") String code,
                                         @PathVariable("provider") String provider) {
        return ResponseEntity.ok().body(oAuth2Service.getUserInfoFromSocial(code, OAuth2Provider.valueOf(provider.toUpperCase())));
    }

}
