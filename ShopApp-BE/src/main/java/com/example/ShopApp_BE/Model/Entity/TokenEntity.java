package com.example.ShopApp_BE.Model.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenEntity extends AbstractEntity {
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
