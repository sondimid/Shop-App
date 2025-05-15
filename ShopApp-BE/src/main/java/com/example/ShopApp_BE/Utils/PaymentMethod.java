package com.example.ShopApp_BE.Utils;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentMethod {
    MOMO("MOMO"),
    PAYOS("PAYOS"),
    COD("COD");

    private final String subject;

    PaymentMethod(String subject) {
        this.subject = subject;
    }

    public static PaymentMethod getPaymentMethod(String method) {
        return Arrays.stream(PaymentMethod.values())
                .filter(e -> e.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(MessageKeys.PAYMENT_IN_VALID));
    }
}
