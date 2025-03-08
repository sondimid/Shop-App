package com.example.ShopApp_BE.Model.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private String fullName;

    private String email;

    private String phoneNumber;

    private String address;

    private String note;

    private String orderDate;

    private String status;

    private Double totalMoney;

    private String shippingMethod;

    private String shippingAddress;

    private String shippingDate;

    private String trackingNumber;

    private String paymentMethod;

    private Long userId;


}
