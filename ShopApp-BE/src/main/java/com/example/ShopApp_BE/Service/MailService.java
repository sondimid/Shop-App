package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;

public interface MailService {
    void sendMailOrderCreate(OrderEntity orderEntity) throws Exception;
    void sendEmailOrder(OrderEntity orderEntity) throws Exception;
    void sendEmailResetPassword(String url, UserEntity userEntity) throws Exception;
    void sendEmailOtp(UserEntity userEntity, String otp) throws Exception;
}
