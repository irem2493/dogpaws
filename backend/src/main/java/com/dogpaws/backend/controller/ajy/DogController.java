package com.dogpaws.backend.controller.ajy;

import com.dogpaws.backend.dto.ajy.*;
import com.dogpaws.backend.entity.ajy.Dog;
import com.dogpaws.backend.service.ajy.DogService;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.ajy.JoinService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/dog")
public class DogController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${front.file-dir}")
    private String fileDir;

    private final JoinService joinService;
    private final DogService dogService;

    @GetMapping("/dogList/{username}")
    public List<DogDto> getDogList(@PathVariable String username) {
        return dogService.getDogs(username);
    }

    @GetMapping("/mypage/dogList/{username}")
    public  List<DogResponseDto> getDogList2(@PathVariable String username) {
        return dogService.getDogList(username);
    }

    @GetMapping("/detail/{dogId}")
    public DogResponseDto getDog(@PathVariable Integer dogId) {
        System.out.println(dogService.getDog(dogId));
        return dogService.getDog(dogId);
    }

    //강아지 정보 삭제
   @DeleteMapping("/{dogId}/{username}")
    public ApiResponse<?> deleteDog(@PathVariable("dogId") Integer dogId, @PathVariable("username") String username) {
        if(dogService.deleteDog(dogId, username)){
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "강아지 삭제 완료");
        }
        else{
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "강아지 삭제 실패");
        }
   }

    @PostMapping("/{username}")
    public ApiResponse<?> registerDog(@PathVariable String username,
                                   @ModelAttribute DogRequestDto dogRequestDto,
                                   @RequestParam(value = "fileInput1", required = false) MultipartFile file1,
                                   @RequestParam(value = "fileInput2", required = false) MultipartFile file2,
                                   @RequestParam(value = "fileInput3", required = false) MultipartFile file3,
                                   HttpSession session) throws IOException {
        log.info("여기는 백 컨트롤러 dogStep1 / dogRequestDto 값: {}", dogRequestDto);
        dogRequestDto.setUsername(username);

        // 이미지 처리
        MultipartFile profileImage = dogRequestDto.getProfileImage();

        if (!profileImage.isEmpty()) {
            String originalFilename = profileImage.getOriginalFilename();
            String fileExt = joinService.getFileExtension(originalFilename);
            Long fileSize = profileImage.getSize();
            String fileNameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
            String newFileName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

            // 파일 저장
            Path targetPath = Paths.get(uploadDir, newFileName + fileExt);
            Files.copy(profileImage.getInputStream(), targetPath);

            // 세션에 저장할 파일 정보 (경로만 저장)
            dogRequestDto.setFileOldName(fileNameWithoutExt);
            dogRequestDto.setFileNewName(newFileName);
            dogRequestDto.setFileSize(fileSize);
            dogRequestDto.setFileExt(fileExt);
            dogRequestDto.setProfileUrl(fileDir + newFileName + fileExt);

            // 1. 파일 데이터를 리스트에 담음
            Map<MultipartFile, String> fileTypeMap = new LinkedHashMap<>();

            if (file1 != null && !file1.isEmpty()) {
                fileTypeMap.put(file1, "PE");
            }

            if (file2 != null && !file2.isEmpty()) {
                fileTypeMap.put(file2, "VA");
            }

            if (file3 != null && !file3.isEmpty()) {
                fileTypeMap.put(file3, "HE");
            }

            dogRequestDto.setFileTypeMap(fileTypeMap);
        }
        joinService.dogRegister(dogRequestDto);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, dogRequestDto);
    }

    @PutMapping("/{username}")
    public ApiResponse<?> updateDog(@PathVariable String username,
                                    @ModelAttribute DogRequestDto dogRequestDto,
                                    @RequestParam(value = "fileInput1", required = false) MultipartFile file1,
                                    @RequestParam(value = "fileInput2", required = false) MultipartFile file2,
                                    @RequestParam(value = "fileInput3", required = false) MultipartFile file3,
                                    HttpSession session) throws IOException {
        log.info("여기는 백 컨트롤러 updateDog / dogRequestDto 값: {}", dogRequestDto);
        dogRequestDto.setUsername(username);

        // 이미지 처리
        MultipartFile profileImage = dogRequestDto.getProfileImage();

        if (!profileImage.isEmpty()) {
            String originalFilename = profileImage.getOriginalFilename();
            String fileExt = joinService.getFileExtension(originalFilename);
            Long fileSize = profileImage.getSize();
            String fileNameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
            String newFileName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

            // 파일 저장
            Path targetPath = Paths.get(uploadDir, newFileName + fileExt);
            Files.copy(profileImage.getInputStream(), targetPath);

            // 세션에 저장할 파일 정보 (경로만 저장)
            dogRequestDto.setFileOldName(fileNameWithoutExt);
            dogRequestDto.setFileNewName(newFileName);
            dogRequestDto.setFileSize(fileSize);
            dogRequestDto.setFileExt(fileExt);
            dogRequestDto.setProfileUrl(fileDir + newFileName + fileExt);

            // 1. 파일 데이터를 리스트에 담음
            Map<MultipartFile, String> fileTypeMap = new LinkedHashMap<>();

            if (file1 != null && !file1.isEmpty()) {
                fileTypeMap.put(file1, "PE");
            }

            if (file2 != null && !file2.isEmpty()) {
                fileTypeMap.put(file2, "VA");
            }

            if (file3 != null && !file3.isEmpty()) {
                fileTypeMap.put(file3, "HE");
            }

            dogRequestDto.setFileTypeMap(fileTypeMap);
        }
        joinService.dogUpdate(dogRequestDto);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, dogRequestDto);
    }

    @GetMapping("/nearby")
    public ApiResponse<?> getNearbyDogs(
            @RequestParam String username,
            @RequestParam(defaultValue = "1") double distance) {

        List<Dog> dogs = dogService.getNearbyDogs(username, distance);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, dogs);
    }
}
