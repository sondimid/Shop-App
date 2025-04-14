package com.example.ShopApp_BE.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "backlist_tokens")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlackListTokenEntity extends AbstractEntity {
    @Column(name = "access_token")
    private String accessToken;
}
