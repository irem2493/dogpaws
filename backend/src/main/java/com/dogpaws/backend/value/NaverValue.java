package com.dogpaws.backend.value;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class NaverValue {
    @Value("${naver.client-id}")
    private String naverClientId;

    @Value("${naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${naver.naverClientSecret}")
    private String naverClientSecret;
}
