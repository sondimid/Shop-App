package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.MessageChatBot;
import com.example.ShopApp_BE.DTO.MessageDTO;
import com.example.ShopApp_BE.Model.Entity.MessageEntity;
import com.example.ShopApp_BE.Model.Response.MessageResponse;
import com.example.ShopApp_BE.Service.ChatService;
import com.example.ShopApp_BE.Service.MessageService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class ChatController {
    private final ChatService chatService;
    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("${api.prefix}/chat")
    public ResponseEntity<String> query(@RequestParam String message) {
        return ResponseEntity.ok().body(chatService.query(message).getChoices().get(0).getMessage().getContent());
    }

    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public MessageResponse handleMessage(@DestinationVariable Long roomId,
                              @RequestBody MessageDTO messageDTO) throws NotFoundException {
        MessageEntity messageEntity = messageService.createMessage(messageDTO);
        return MessageResponse.fromMessageEntity(messageEntity);
    }

    @GetMapping("${api.prefix}/chat/{senderId}/{recipientId}")
    public ResponseEntity<?> getMessages(@PathVariable("senderId") Long senderId,
                                        @PathVariable("recipientId") Long recipientId) throws NotFoundException {
        return ResponseEntity.ok(messageService.getMessages(senderId, recipientId));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("${api.prefix}/chat/all")
    public ResponseEntity<?> getAllMessages(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) throws NotFoundException {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

}
