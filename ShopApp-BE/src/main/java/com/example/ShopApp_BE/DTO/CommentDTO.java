package com.example.ShopApp_BE.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO{
    private String content;

    private Long productId;

    private MultipartFile image;
}
