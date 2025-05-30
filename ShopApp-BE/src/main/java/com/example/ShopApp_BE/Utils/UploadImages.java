package com.example.ShopApp_BE.Utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RequiredArgsConstructor
public class UploadImages {
    @Value("${file.dir}")
    private static String DIR;

    @Value("${file.url}")
    private static String URL;

    public static String uploadImage(MultipartFile file) throws IOException {
        Path uploadPath  = Paths.get(DIR);
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        file.transferTo(new File(DIR + fileName));
        return URL + fileName;
    }
}
