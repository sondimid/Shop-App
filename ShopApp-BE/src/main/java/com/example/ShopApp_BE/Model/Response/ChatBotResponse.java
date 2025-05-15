package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.DTO.MessageChatBot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatBotResponse extends AbstractResponse{
    private List<Choice> choices;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private int index;
        private MessageChatBot message;
    }
}
