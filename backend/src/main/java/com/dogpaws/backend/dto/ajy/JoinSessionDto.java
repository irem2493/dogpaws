package com.dogpaws.backend.dto.ajy;

import com.dogpaws.backend.dto.common.FileDto;
import lombok.Data;

@Data
public class JoinSessionDto {
    private UserRequestDto step1Data;
    private DogRequestDto step2Data;
    private FileDto step4Data;
}
