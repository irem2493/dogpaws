package com.dogpaws.backend.controller.ajy;

import com.dogpaws.backend.dto.ajy.UserRequestDto;
import com.dogpaws.backend.entity.ajy.User;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.ajy.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/check-user")
    public ApiResponse<?> checkUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userService.findByUsername(username);

        System.out.println("check-user : " + user);

        if(user != null && passwordEncoder.matches(password,user.getPassword())) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, user);
        }

        return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
    }

    @PutMapping("/edit-user")
    public ApiResponse<?> editUser(@ModelAttribute UserRequestDto userRequestDto) {
        boolean result =  userService.updateUser(userRequestDto);

        if(result)
          return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "개인정보 수정 완료");
        else return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
    }

    @PutMapping("/delete-user")
    public ApiResponse<?> deleteUser(@RequestParam("username") String username) {
        boolean result = userService.deleteUser(username);
        if(result)
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "회원탈퇴 완료");
        else return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
    }
}
