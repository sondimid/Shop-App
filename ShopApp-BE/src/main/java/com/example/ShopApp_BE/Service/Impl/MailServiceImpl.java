package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Service.MailService;
import com.example.ShopApp_BE.Utils.JwtProperties;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine thymeleafEngine;
    private final JwtProperties jwtProperties;

    public void sendMailOrderCreate(OrderEntity orderEntity) throws Exception {
        Context context = new Context();
        context.setVariable("fullName", orderEntity.getFullName());
        context.setVariable("orderId", orderEntity.getCode());
        context.setVariable("status", OrderStatus.fromString(orderEntity.getStatus()).getEmailSubject());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy/HH/mm");
        String formattedDate = orderEntity.getOrderDate().format(formatter);
        context.setVariable("orderDate", formattedDate);


        String htmlContent = thymeleafEngine.process("email/order-create", context);

        sendEmail(orderEntity.getUserEntity().getEmail(), orderEntity.getStatus(), htmlContent);
    }


    public void sendEmailOrder(OrderEntity orderEntity) throws Exception {
        Context context = new Context();
        context.setVariable("fullName", orderEntity.getFullName());
        context.setVariable("orderId", orderEntity.getCode());
        context.setVariable("status", OrderStatus.fromString(orderEntity.getStatus()).getEmailSubject());
        context.setVariable("orderDate", orderEntity.getOrderDate());

        String htmlContent = thymeleafEngine.process("email/order-status", context);

        sendEmail(orderEntity.getUserEntity().getEmail(), orderEntity.getStatus(), htmlContent);
    }

    @Override
    public void sendEmailResetPassword(String url, UserEntity userEntity) throws Exception {
        Context context = new Context();
        context.setVariable("url", url);
        context.setVariable("fullName", userEntity.getFullName());
        context.setVariable("expiration", jwtProperties.getExpirationResetToken() / 60);
        String htmlContent = thymeleafEngine.process("email/reset-password", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("daongocson12022004@gmail.com");
        helper.setTo(userEntity.getEmail());
        helper.setSubject(MessageKeys.RESET_PASSWORD);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Override
    public void sendEmailOtp(UserEntity userEntity, String otp) throws Exception {
        Context context = new Context();
        context.setVariable("fullName", userEntity.getFullName());
        context.setVariable("otp", otp);

        String htmlContent = thymeleafEngine.process("email/otp", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("daongocson12022004@gmail.com");
        helper.setTo(userEntity.getEmail());
        helper.setText(htmlContent, true);
        helper.setSubject("OTP CODE");
        mailSender.send(message);
    }

    private void sendEmail(String to, String subject, String body) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("daongocson12022004@gmail.com");
        helper.setTo(to);
        helper.setSubject(OrderStatus.fromString(subject).getEmailSubject());
        helper.setText(body, true);

        mailSender.send(message);
    }
}