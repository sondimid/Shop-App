package com.example.ShopApp_BE.Utils;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderStatus {
    PENDING("ĐƠN HÀNG CỦA BẠN ĐANG CHỜ XÁC NHẬN"),
    CONFIRMED("ĐƠN HÀNG ĐÃ ĐƯỢC XÁC NHẬN"),
    PACKAGED("ĐƠN HÀNG CỦA BẠN ĐÃ ĐƯỢC ĐÓNG GÓI"),
    IN_TRANSIT("ĐƠN HÀNG ĐANG TRÊN ĐƯỜNG GIAO"),
    COMPLETED("ĐƠN HÀNG ĐÃ GIAO THÀNH CÔNG"),
    CANCELLED("ĐƠN HÀNG CỦA BẠN ĐÃ BỊ HỦY");

    private final String emailSubject;

    OrderStatus(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public static OrderStatus fromString(String status) throws Exception {
        return Arrays.stream(OrderStatus.values())
                .filter(e -> e.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new Exception(MessageKeys.STATUS_IN_VALID));
    }
}
