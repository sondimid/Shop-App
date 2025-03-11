package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.DTO.*;
import com.example.ShopApp_BE.Service.TokenService;
import com.example.ShopApp_BE.Service.UserService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        try{
            return ResponseEntity.accepted().body(userService.login(userLoginDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        try{
            userService.createUser(userRegisterDTO);
            return ResponseEntity.accepted().body(MessageKeys.REGISTER_SUCCESS);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profiles")
    public ResponseEntity<?> update(@Valid @RequestBody UserUpdateDTO userUpdateDTO,
                                    @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) {
        String token = authorization.substring(7);
        try{
            userService.update(userUpdateDTO, token);
            return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO,
                                            @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization){
        String token = authorization.substring(7);
        try {
            userService.changePassword(userChangePasswordDTO, token);
            return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("refresh_token") String refreshToken) {
        try{
            return ResponseEntity.accepted().body(tokenService.refreshToken(refreshToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) {
        String accessToken = authorization.substring(7);
        try{
            return ResponseEntity.accepted().body(tokenService.deleteToken(accessToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profiles")
    public ResponseEntity<?> getUserDetails(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) {
        String token = authorization.substring(7);
        try{
            return ResponseEntity.ok().body(userService.getUserDetails(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization,
                                        @RequestBody MultipartFile file) throws Exception {
        try {
            return ResponseEntity.accepted()
                    .body(userService.uploadAvatar(authorization.substring(7), file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
