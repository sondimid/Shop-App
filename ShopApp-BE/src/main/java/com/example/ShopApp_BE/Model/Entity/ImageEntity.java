package com.example.ShopApp_BE.Model.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ImageEntity extends AbstractEntity {
    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;
}
