package com.example.ShopApp_BE.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity extends AbstractEntity{
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "final_price")
    private Double finalPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

    @OneToMany(mappedBy = "productEntity",cascade = CascadeType.ALL)
    private List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL)
    private List<CommentEntity> commentEntities = new ArrayList<>();

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL)
    private List<ImageEntity> imageEntities = new ArrayList<>();

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL)
    private List<CartDetailEntity> cartDetailEntities =  new ArrayList<>();
}
