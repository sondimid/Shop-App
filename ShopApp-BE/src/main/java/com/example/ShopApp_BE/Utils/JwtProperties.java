package com.example.ShopApp_BE.Utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String refreshKey;
    private String secretKey;
    private String resetKey;
    private Integer expiration;
    private Integer expirationRefreshToken;
    private Integer expirationResetToken;
}
