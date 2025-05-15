package com.example.ShopApp_BE.Utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
public enum OrderStatus {
    PENDING("ĐƠN HÀNG CỦA BẠN ĐANG CHỜ XÁC NHẬN",1),
    CONFIRMED("ĐƠN HÀNG ĐÃ ĐƯỢC XÁC NHẬN",2),
    PACKAGED("ĐƠN HÀNG CỦA BẠN ĐÃ ĐƯỢC ĐÓNG GÓI",3),
    IN_TRANSIT("ĐƠN HÀNG ĐANG TRÊN ĐƯỜNG GIAO",4),
    COMPLETED("ĐƠN HÀNG ĐÃ GIAO THÀNH CÔNG",5),
    CANCELLED("ĐƠN HÀNG CỦA BẠN ĐÃ BỊ HỦY",6);

    private final String emailSubject;
    private final Integer statusCode;

    OrderStatus(String emailSubject, Integer statusCode) {
        this.emailSubject = emailSubject;
        this.statusCode = statusCode;
    }

    public static OrderStatus fromString(String status) {
        return Arrays.stream(OrderStatus.values())
                .filter(e -> e.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(MessageKeys.STATUS_IN_VALID));
    }
}
