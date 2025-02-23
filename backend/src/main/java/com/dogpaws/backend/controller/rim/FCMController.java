package com.dogpaws.backend.controller.rim;


import com.dogpaws.backend.service.rim.NotificationService;
import com.dogpaws.backend.utils.JWTUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FCMController {

    private final JWTUtil jwtUtil;
    private final NotificationService notificationService;

    @PostMapping("/token")
    public ResponseEntity<Void> saveFCMToken(
            @RequestBody FCMTokenRequest request) {
        try {
            log.info("FCM 토큰 저장 요청 수신: {}", request);
            notificationService.saveFcmToken(request.getUsername(), request.getToken());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("FCM 토큰 저장 실패", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/token")
    public ResponseEntity<Void> deleteFCMToken(
            @RequestParam String username) {

        notificationService.deleteFcmToken(username);
        return ResponseEntity.ok().build();
    }

//    private String getUsernameFromToken(String token) {
//        // JWTUtil을 사용하여 토큰에서 username 추출
//        return jwtUtil.getUsername(token);
//    }
}

@Data
class FCMTokenRequest {
    private String token;
    private String username;
}