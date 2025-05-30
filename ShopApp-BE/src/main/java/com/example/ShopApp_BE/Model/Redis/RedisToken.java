package com.example.ShopApp_BE.Model.Redis;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("RedisToken")
public class RedisToken implements Serializable {
    private String id;

    private String accessToken;

    private String refreshToken;

}
