package com.example.ShopApp_BE.Utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payos")
public class PayOSProperties {
    private String clientId;

    private String apiKey;

    private String checksumKey;

}
