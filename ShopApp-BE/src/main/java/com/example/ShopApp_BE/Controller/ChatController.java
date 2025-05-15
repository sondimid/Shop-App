package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.Service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/chat")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("")
    public ResponseEntity<String> query(@RequestParam String message) {
        return ResponseEntity.ok().body(chatService.query(message).getChoices().get(0).getMessage().getContent());
    }
}
