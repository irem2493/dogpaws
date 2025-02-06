package com.dogpaws.backend.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenUserDto {
    private String username;
    private String nickname;
    private String role;
}
