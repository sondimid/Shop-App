package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.UserEntity;
import lombok.*;

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

    private String phoneNumber;

    private String address;

    private String password;

    private String dateOfBirth;

    private String avatar;

    private Integer facebookAccountId;

    private Integer googleAccountId;

    private String role;

    public static UserResponse fromUserEntity(UserEntity userEntity) {
        UserResponse userResponse =  UserResponse.builder()
                .Id(userEntity.getId())
                .fullName(userEntity.getFullName())
                .phoneNumber(userEntity.getPhoneNumber())
                .address(userEntity.getAddress())
                .dateOfBirth(userEntity.getDateOfBirth())
                .avatar(userEntity.getAvatarUrl())
                .facebookAccountId(userEntity.getFacebookAccountId())
                .googleAccountId(userEntity.getGoogleAccountId())
                .role(userEntity.getRoleEntity().getRole())
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
