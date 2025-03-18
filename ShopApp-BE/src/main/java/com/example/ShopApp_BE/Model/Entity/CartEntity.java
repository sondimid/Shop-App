package com.example.ShopApp_BE.Model.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "carts")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartEntity extends AbstractEntity{
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "cartEntity", cascade = CascadeType.ALL)
    private List<CartDetailEntity> cartDetailEntities;

}
