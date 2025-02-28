package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.DTO.OrderDTO;
import com.example.ShopApp_BE.Model.DTO.RoleDTO;

import java.util.List;

public class UserResponse {
    private String fullName;

    private String phoneNumber;

    private String address;

    private String password;

    private String dateOfBirth;

    private Integer facebookAccountId;

    private Integer googleAccountId;

    private List<OrderDTO> orderDTOs;

    private RoleDTO roleDTO;

}
