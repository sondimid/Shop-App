package com.example.ShopApp_BE.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatBotDTO {
    private String model;

    private List<MessageChatBot> messages;
}
