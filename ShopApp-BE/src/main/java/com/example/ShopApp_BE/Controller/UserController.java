package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.DTO.*;
import com.example.ShopApp_BE.Service.OAuth2Service;
import com.example.ShopApp_BE.Service.TokenService;
import com.example.ShopApp_BE.Service.UserService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.OAuth2Provider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;
    private final OAuth2Service oAuth2Service;

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {

        return ResponseEntity.accepted().body(userService.login(userLoginDTO));

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {

        userService.createUser(userRegisterDTO);
        return ResponseEntity.accepted().body(MessageKeys.REGISTER_SUCCESS);

    }

    @PutMapping("/profiles")
    public ResponseEntity<?> update(@Valid @RequestBody UserUpdateDTO userUpdateDTO,
                                    @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) {
        String token = authorization.substring(7);

        userService.update(userUpdateDTO, token);
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);

    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO,
                                            @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization){
        String token = authorization.substring(7);

        userService.changePassword(userChangePasswordDTO, token);
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader(MessageKeys.REFRESH_TOKEN_HEADER) String refreshToken) throws Exception {

        return ResponseEntity.accepted().body(tokenService.refreshToken(refreshToken));

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(MessageKeys.REFRESH_TOKEN_HEADER) String refreshToken) throws Exception {
        tokenService.deleteToken(refreshToken);
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
    public ResponseEntity<?> forgotPassword(@RequestBody EmailDTO emailDTO) throws Exception {
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
