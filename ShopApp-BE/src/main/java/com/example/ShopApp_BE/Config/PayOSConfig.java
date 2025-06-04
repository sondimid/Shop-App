package com.example.ShopApp_BE.Config;

import com.example.ShopApp_BE.Utils.PayOSProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
@RequiredArgsConstructor
public class PayOSConfig {

    @Value("${payos.api-key}")
    String apiKey;

    @Value("${payos.client-id}")
    String clientId;

    @Value("${payos.checksum-key}")
    String checksumKey;

    @Bean
    public PayOS payOS() {
        return new PayOS(apiKey, clientId, checksumKey);
    }
}
