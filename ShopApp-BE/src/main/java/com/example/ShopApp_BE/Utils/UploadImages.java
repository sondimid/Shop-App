package com.example.ShopApp_BE.Utils;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RequiredArgsConstructor
public class UploadImages {
    private static final String DIR = "D:/uploads/ShopApp-BE/";
    private static final String URL = "http://localhost:8080/uploads/";
    public static String uploadImage(MultipartFile file) throws IOException {
        Path uploadPath  = Paths.get(DIR);
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        file.transferTo(new File(DIR + fileName));
        return URL + fileName;
    }
}
