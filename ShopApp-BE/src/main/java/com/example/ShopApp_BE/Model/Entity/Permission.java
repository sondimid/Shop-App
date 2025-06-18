package com.example.ShopApp_BE.Model.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "permission")

public class Permission extends AbstractEntity{
    @ManyToMany
    @JoinColumn()
}
