package com.dogpaws.backend.dto.ajy;

import com.dogpaws.backend.dto.common.FileDto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
public class JoinSessionDto {
    private UserRequestDto step1Data;
    private DogRequestDto step2Data;
    private Map<MultipartFile, String> step3Data;
}
