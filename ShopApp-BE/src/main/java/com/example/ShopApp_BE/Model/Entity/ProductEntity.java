package com.example.ShopApp_BE.Model.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductEntity extends AbstractEntity implements Serializable {
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Min(value = 0)
    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "discount")
    @Min(value = 0)
    @Max(value = 50)
    private Double discount;

    @Column(name = "quantity")
    @Min(value = 0)
    private Long quantity;

    @Column(name = "description")
    @Lob
    private String description;

    @Column(name = "final_price")
    @Min(value = 0)
    private Double finalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

    @OneToMany(mappedBy = "productEntity",cascade = CascadeType.ALL)
    private List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL)
    private List<CommentEntity> commentEntities = new ArrayList<>();

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImageEntity> imageEntities = new ArrayList<>();

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL)
    private List<CartDetailEntity> cartDetailEntities =  new ArrayList<>();
}
