package com.dogpaws.backend.controller.ajy;

import com.dogpaws.backend.global.common.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/upload")
public class BoardFileController {

    @Value("${file.upload-dir}")
    private String uploadDir;


    @Value("${front.file-dir}")
    private String fileDir;

    @PostMapping
    public ApiResponse<?> uploadFile(@RequestParam("upload") MultipartFile file) {
        try {
            // ✅ 파일을 기본 저장 폴더에 즉시 저장
            Files.createDirectories(Paths.get(uploadDir));

            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(uploadDir + filename);
            Files.write(filepath, file.getBytes());


            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, fileDir + filename);
        } catch (Exception e) {
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "파일 업로드 실패");
        }
    }
}
