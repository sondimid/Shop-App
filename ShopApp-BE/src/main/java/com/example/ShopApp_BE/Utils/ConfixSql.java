package com.example.ShopApp_BE.Utils;

public class ConfixSql {

    public interface Product{
        String SEARCH_BY_CATEGORY = "SELECT p FROM ProductEntity p WHERE p.categoryEntity.id = :categoryId" +
                " AND (p.name LIKE CONCAT('%', :keyword, '%') OR p.description LIKE CONCAT('%', :keyword, '%') ) " +
                "AND p.finalPrice >= :fromPrice AND p.finalPrice <= :toPrice  ";

        String SEARCH_BY_KEYWORD = "SELECT p FROM ProductEntity p WHERE (" +
                " p.name LIKE CONCAT('%', :keyword, '%')" +
                " OR p.categoryEntity.name LIKE CONCAT('%', :keyword, '%') )" +
                " AND p.price >= :fromPrice AND p.price <= :toPrice";
        String SEARCH_BY_ID = "SELECT p FROM ProductEntity p LEFT JOIN FETCH p.categoryEntity WHERE p.id = :id";

        String SEARCH = "SELECT p FROM ProductEntity p";
    }

    public interface Category{
        String SEARCH_BY_KEYWORD = "SELECT o FROM OrderEntity o WHERE o.status = :keyword " +
                " OR o.shippingAddress LIKE CONCAT('%', :keyword, '%') OR o.paymentMethod LIKE CONCAT('%', :keyword, '%')";


        String SEARCH_BY_USER_AND_KEYWORD = "SELECT o FROM OrderEntity o WHERE o.userEntity.id = :userId " +
                " AND ( o.status = :keyword OR o.shippingAddress LIKE CONCAT('%', :keyword, '%')" +
                " OR o.paymentMethod LIKE CONCAT('%', :keyword, '%') )";

    }

    public interface CartDetail{
        String SEARCH_BY_CART = "SELECT c FROM CartDetailEntity c WHERE c.cartEntity.id = :cartId ORDER BY c.createdAt DESC," +
                " c.updatedAt DESC";
    }

    public interface User{
        String SEARCH_BY_KEYWORD = "SELECT u from UserEntity u WHERE (u.fullName LIKE CONCAT('%', :keyword, '%')" +
                " OR u.email LIKE CONCAT('%', :keyword , '%') OR u.phoneNumber LIKE CONCAT('%', :keyword , '%') ) AND (u.roleEntity.role = 'USER')";
    }

    public interface Message{
        String SEARCH_BY_CREATED_AT = "SELECT m from MessageEntity m WHERE m.createdAt = (select max(m1.createdAt) from MessageEntity m1 where m.roomId = m1.roomId) order by m.createdAt DESC limit 5";
    }
}
