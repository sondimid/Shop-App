package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.MessageDTO;
import com.example.ShopApp_BE.Model.Entity.MessageEntity;
import com.example.ShopApp_BE.Model.Response.MessageResponse;

import java.util.List;

public interface MessageService {
    MessageEntity createMessage(MessageDTO messageDTO) throws NotFoundException;

    List<MessageResponse> getMessages(Long senderId, Long recipientId) throws NotFoundException;

    List<MessageResponse> getAllMessages();
}
