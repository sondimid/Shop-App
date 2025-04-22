package com.example.ShopApp_BE.Config;

import com.example.ShopApp_BE.Utils.PayOSProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
@RequiredArgsConstructor
public class PayOSConfig {

    private final PayOSProperties payOSProperties;

    @Bean
    public PayOS payOS() {
        return new PayOS(payOSProperties.getClientId(), payOSProperties.getApiKey(), payOSProperties.getChecksumKey());
    }
}
