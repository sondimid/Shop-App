package com.example.ShopApp_BE.Model.Entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "cart_details")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailEntity extends AbstractEntity{
    @Column(name = "number_of_product", nullable = false)
    private Integer numberOfProducts;

    @Column(name = "color", length = 20)
    private String color;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private CartEntity cartEntity;
}
