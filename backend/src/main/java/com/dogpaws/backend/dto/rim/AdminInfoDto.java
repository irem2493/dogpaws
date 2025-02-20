package com.dogpaws.backend.dto.rim;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminInfoDto {
    private String username;
    private String nickname;
    private String role;
}
