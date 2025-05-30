package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.MessageDTO;
import com.example.ShopApp_BE.Model.Entity.MessageEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.MessageResponse;
import com.example.ShopApp_BE.Repository.MessageRepository;
import com.example.ShopApp_BE.Repository.UserRepository;
import com.example.ShopApp_BE.Service.MessageService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public MessageEntity createMessage(MessageDTO messageDTO) throws NotFoundException {
        MessageEntity messageEntity = modelMapper.map(messageDTO, MessageEntity.class);
        messageEntity.setIsRead(Boolean.FALSE);
        messageEntity.setRoomId(messageDTO.getRecipientId() + messageDTO.getSenderId());
        UserEntity sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));

        UserEntity recipient = userRepository.findById(messageDTO.getRecipientId())
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        messageEntity.setSender(sender);
        messageEntity.setRecipient(recipient);
        return messageRepository.save(messageEntity);
    }



    @Override
    public List<MessageResponse> getAllMessages() {
        return messageRepository.findLatest().stream().map(MessageResponse::fromMessageEntity).toList();
    }

    @Override
    public List<MessageResponse> getMessages(Long senderId, Long recipientId) throws NotFoundException {
        UserEntity sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));

        UserEntity recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));

        List<MessageEntity> messageEntity = messageRepository.findByRoomId(senderId + recipientId)
                .orElse(List.of(MessageEntity.builder()
                        .sender(sender)
                        .isRead(Boolean.FALSE)
                        .content("")
                        .recipient(recipient)
                        .build()));
        return messageEntity.stream().map(MessageResponse::fromMessageEntity).toList();
    }
}
