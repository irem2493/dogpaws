package com.dogpaws.backend.dto.rim.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class ProductRegistRequest {
    private String productDtoString;
    private String optionDtosString;
    private MultipartFile thumbnailImage;
    private MultipartFile detailImage;
    private String userName;
}