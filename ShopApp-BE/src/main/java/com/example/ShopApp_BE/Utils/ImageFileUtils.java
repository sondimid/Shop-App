package com.example.ShopApp_BE.Utils;

import jakarta.persistence.Column;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
@Component
public class ImageFileUtils {
    public static Boolean isImageFile(MultipartFile file) {
        return file.getContentType() != null && file.getContentType().startsWith("image/");
    }
}
