package com.example.ShopApp_BE.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @NotBlank
    private String fullName;

    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String address;

    private String note;

    private Double totalMoney;

    private LocalDateTime orderDate;

    private String shippingMethod;

    private String shippingAddress;
    
    private LocalDateTime shippingDate;

    private String trackingNumber;

    @NotBlank
    private String paymentMethod;

    private Long code;

    private Boolean active;

    private List<OrderDetailDTO> orderDetailDTOs;
}
