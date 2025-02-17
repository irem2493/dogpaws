package com.dogpaws.backend.dto.rim.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartOptionResponseDto {
    private Long cartItemId;
    private Long optionId;
    private String optionName;
    private int optionPrice;
    private int quantity;
}