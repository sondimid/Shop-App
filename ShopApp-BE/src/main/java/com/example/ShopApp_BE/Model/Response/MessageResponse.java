package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.MessageEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse extends AbstractResponse{
    private String content;

    private Long senderId;

    private Long recipientId;

    private String senderName;

    private String recipientName;

    private Boolean isRead;

    public static MessageResponse fromMessageEntity(MessageEntity messageEntity) {
        MessageResponse messageResponse =  MessageResponse.builder()
                .content(messageEntity.getContent())
                .senderId(messageEntity.getSender().getId())
                .recipientId(messageEntity.getRecipient().getId())
                .senderName(messageEntity.getSender().getFullName())
                .recipientName(messageEntity.getRecipient().getFullName())
                .isRead(messageEntity.getIsRead()).build();
        messageResponse.setCreatedAt(messageEntity.getCreatedAt());
        return messageResponse;
    }
}
