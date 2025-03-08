package com.example.ShopApp_BE.DTO;



import com.example.ShopApp_BE.Model.Response.UserResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class RoleDTO {
    private String name;

    private List<UserResponse> userDTOS;
}
