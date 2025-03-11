package com.example.ShopApp_BE.Utils;

public class ConfixSql {

    public interface Product{
        String SEARCH_BY_CATEGORY = "SELECT p FROM ProductEntity p WHERE p.categoryEntity.id = :categoryId";
    }
}
