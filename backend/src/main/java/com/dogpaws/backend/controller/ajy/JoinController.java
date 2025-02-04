package com.dogpaws.backend.controller.ajy;

import com.dogpaws.backend.dto.ajy.DogRequestDto;
import com.dogpaws.backend.dto.ajy.JoinSessionDto;
import com.dogpaws.backend.dto.ajy.UserRequestDto;
import com.dogpaws.backend.dto.common.FileDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.ajy.JoinService;
import com.dogpaws.backend.service.common.FileService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/join")
@RequiredArgsConstructor
@Slf4j
@SessionAttributes("joinData")
public class JoinController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${front.file-dir}")
    private String fileDir;

    private final JoinService joinService;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/step1")
    public ApiResponse<?> step1(@ModelAttribute UserRequestDto userRequestDto, HttpSession session) throws IOException {
        //log.info("여기는 백 컨트롤러 step1 / userRequestDto 값: {}", userRequestDto);

        String encryptedPassword = passwordEncoder.encode(userRequestDto.getPassword());
        userRequestDto.setPassword(encryptedPassword);

        JoinSessionDto sessionData = (JoinSessionDto) session.getAttribute("joinSession");

        if (sessionData == null) {
            sessionData = new JoinSessionDto();
        }

        sessionData.setStep1Data(userRequestDto);
        session.setAttribute("joinSession", sessionData);

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "1단계 저장 완료");
    }

    @PostMapping("/step1/data")
    public ApiResponse<?> getStep1Data(HttpSession session) throws IOException {
        // 세션에서 JoinSessionDto 가져오기
        JoinSessionDto sessionData = (JoinSessionDto) session.getAttribute("joinSession");

        // 세션에 데이터가 없는 경우 처리
        if (sessionData == null || sessionData.getStep1Data() == null) {
            log.warn("세션에 저장된 1단계 데이터가 없습니다.");
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "저장된 데이터 없음");
        }

        // 1단계 데이터 반환
        UserRequestDto step1Data = sessionData.getStep1Data();
        log.info("세션에서 1단계 데이터 반환: {}", step1Data);

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, step1Data);
    }

    @PostMapping("/step2")
    public ApiResponse<?> step2(@ModelAttribute DogRequestDto dogRequestDto, HttpSession session) throws IOException {
        log.info("여기는 백 컨트롤러 step2 / dogRequestDto 값: {}", dogRequestDto);

        JoinSessionDto sessionData = (JoinSessionDto) session.getAttribute("joinSession");

        // 세션에 데이터가 없는 경우 처리
        if (sessionData == null || sessionData.getStep1Data() == null) {
            log.warn("세션에 저장된 1단계 데이터가 없습니다.");
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "저장된 데이터 없음");
        }

        // 1단계 데이터 반환
        UserRequestDto step1Data = sessionData.getStep1Data();

        String username = step1Data.getUsername();
        dogRequestDto.setUsername(username);

        // 프로필 이미지 URL 저장 로직 확인
        if (dogRequestDto.getProfileUrl() != null) {
            log.info("프로필 이미지 URL 저장: {}", dogRequestDto.getProfileUrl());
        }

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
            System.out.println(targetPath);

            dogRequestDto.setFileOldName(fileNameWithoutExt);
            dogRequestDto.setFileNewName(newFileName);
            dogRequestDto.setFileSize(fileSize);
            dogRequestDto.setFileExt(fileExt);
            dogRequestDto.setProfileUrl(fileDir + newFileName + fileExt);
        }

        sessionData.setStep2Data(dogRequestDto);
        session.setAttribute("joinSession", sessionData);

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, dogRequestDto);

    }

    @PostMapping("/step2/data")
    public ApiResponse<?> getStep2Data(HttpSession session) throws IOException {
        // 세션에서 JoinSessionDto 가져오기
        JoinSessionDto sessionData = (JoinSessionDto) session.getAttribute("joinSession");

        // 세션에 데이터가 없는 경우 처리
        if (sessionData == null || sessionData.getStep2Data() == null) {
            log.warn("세션에 저장된 2단계 데이터가 없습니다.");
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "저장된 데이터 없음");
        }

        // 1단계 데이터 반환
        DogRequestDto step2Data = sessionData.getStep2Data();

        List<MultipartFile> fileList = step2Data.getActivityImages();

        for(MultipartFile file : fileList) {
            System.out.println("step2 사이즈 : "+file.getSize());
        }

        log.info("세션에서 2단계 데이터 반환: {}", step2Data);



        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, step2Data);
    }

    @PostMapping("/step3")
    public ApiResponse<?> step3(@RequestParam(value = "fileInput1", required = false) MultipartFile file1,
                                @RequestParam(value = "fileInput2", required = false) MultipartFile file2,
                                @RequestParam(value = "fileInput3", required = false) MultipartFile file3,
                                @RequestParam("isMatingAvailable") String isMatingAvailable,
                                HttpSession session) throws IOException {

        log.info("여기는 백 컨트롤러 step3 / isMatingAvailable 값: {}", isMatingAvailable);
        JoinSessionDto sessionData = (JoinSessionDto) session.getAttribute("joinSession");

        if (sessionData == null || sessionData.getStep1Data() == null || sessionData.getStep2Data() == null) {
            log.warn("세션에 저장된 데이터가 부족합니다.");
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "저장된 데이터 없음");
        }

        // 기존 세션 데이터에 값 갱신
        DogRequestDto step2Data = sessionData.getStep2Data();
        step2Data.setIsMatingAvailable(isMatingAvailable);
        sessionData.setStep2Data(step2Data);
        session.setAttribute("joinSession", sessionData);

        // 1. 파일 데이터를 리스트에 담음
        List<MultipartFile> files = new ArrayList<>();

        if (file1 != null && !file1.isEmpty()) {
            files.add(file1);
        }

        if (file2 != null && !file2.isEmpty()) {
            files.add(file2);
        }

        if (file3 != null && !file3.isEmpty()) {
            files.add(file3);
        }

        // 2. 파일 정보를 세션에 저장
        sessionData.setStep3Data(files);
        session.setAttribute("joinSession", sessionData);

        joinService.join(sessionData);

        // 회원가입 완료 처리 로직
        log.info("회원가입이 완료되었습니다.");

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "회원가입 완료");
    }

    @PostMapping("/success-join")
    public ApiResponse<?> invalidateSession(HttpSession session) {
        log.info("세션을 무효화합니다.");
        session.invalidate();
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "세션 무효화 완료");
    }

    @GetMapping("/check/{username}")
    public ApiResponse<?> duplicateCheck(@PathVariable String username) {
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, joinService.duplicateCheck(username));
    }
}
