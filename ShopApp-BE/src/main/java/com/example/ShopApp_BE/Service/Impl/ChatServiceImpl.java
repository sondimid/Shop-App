package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.DTO.ChatBotDTO;
import com.example.ShopApp_BE.DTO.MessageChatBot;
import com.example.ShopApp_BE.Model.Response.ChatBotResponse;
import com.example.ShopApp_BE.Service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    @Value("${chat.api-key}")
    private String API_KEY;

    @Value("${chat.end-point}")
    private String OPEN_API_CHAT_END_POINT;

    @Override
    public ChatBotResponse query(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        MessageChatBot messageChatBot = MessageChatBot.builder()
                .content(message)
                .role("user").build();
        MessageChatBot sysMessage = MessageChatBot.builder()
                .content("Bạn là trợ lý bán hàng cho một cửa hàng chuyên về các sản phẩm điện tử như điện thoại, laptop, tai nghe, và máy tính bảng. " +
                        "Hãy trả lời tất cả các câu hỏi liên quan đến những sản phẩm này, bao gồm thông tin sản phẩm, giá cả, tình trạng còn hàng, cách sử dụng và tư vấn lựa chọn phù hợp. " +
                        "Khi người dùng hỏi một cách chung chung hoặc không rõ ràng, hãy cố gắng hiểu ý định của họ theo hướng là họ đang hỏi về sản phẩm điện tử trong cửa hàng và trả lời phù hợp. " +
                        "Nếu người dùng nói tiếng Anh, vẫn trả lời tự nhiên bằng tiếng Anh với nội dung tương tự. " +
                        "Nếu người dùng hỏi về những chủ đề không liên quan đến đồ điện tử (ví dụ: đồ ăn, sách, du lịch...), hãy từ chối lịch sự bằng cách nói: 'Xin lỗi, tôi chỉ hỗ trợ các sản phẩm điện tử có trong cửa hàng.'")
                .role("system")
                .build();

        ChatBotDTO chatBotDTO = ChatBotDTO.builder()
                .model("openai/gpt-3.5-turbo")
                .messages(List.of(sysMessage, messageChatBot)).build();

        HttpEntity<ChatBotDTO> entity = new HttpEntity<>(chatBotDTO, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(OPEN_API_CHAT_END_POINT, entity, ChatBotResponse.class);
    }
}
