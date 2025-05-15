package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.Model.Response.ChatBotResponse;

public interface ChatService {
    ChatBotResponse query(String message);
}
