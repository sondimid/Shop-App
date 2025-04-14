package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse extends AbstractResponse {
    private Long Id;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String address;

    private String avatar;

    private String facebookAccountId;

    private String googleAccountId;

    private String role;

    private Boolean isSocialLogin;

    public static UserResponse fromUserEntity(UserEntity userEntity) {
        UserResponse userResponse =  UserResponse.builder()
                .Id(userEntity.getId())
                .fullName(userEntity.getFullName())
                .email(userEntity.getEmail())
                .phoneNumber(userEntity.getPhoneNumber())
                .address(userEntity.getAddress())
                .avatar(userEntity.getAvatarUrl())
                .facebookAccountId(userEntity.getFacebookAccountId())
                .googleAccountId(userEntity.getGoogleAccountId())
                .role(userEntity.getRoleEntity().getRole())
                .isSocialLogin(userEntity.getPassword() != null)
                .build();
        userResponse.setCreatedAt(userEntity.getCreatedAt());
        userResponse.setUpdatedAt(userEntity.getUpdatedAt());
        return userResponse;
    }

    public static List<UserResponse> fromUserEntities(List<UserEntity> userEntities) {
        List<UserResponse> userResponses = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            userResponses.add(UserResponse.fromUserEntity(userEntity));
        }
        return userResponses;
    }

}
