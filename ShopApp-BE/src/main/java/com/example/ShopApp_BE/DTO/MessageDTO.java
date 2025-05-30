package com.example.ShopApp_BE.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private String content;

    private Long senderId;

    private Long recipientId;
}
