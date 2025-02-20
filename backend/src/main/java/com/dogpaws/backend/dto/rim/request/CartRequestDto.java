package com.dogpaws.backend.dto.rim.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class CartRequestDto {
    @JsonProperty("productId")
    private Long productId;
    private int quantity;
    private boolean isBaseProduct;
    private List<CartOptionDto> options;
    private String username;

    @Data
    @NoArgsConstructor
    public static class CartOptionDto {
        @JsonProperty("optionId")
        private Long optionId;
        private int quantity;

        @Builder
        public CartOptionDto(Long optionId, int quantity) {
            this.optionId = optionId;
            this.quantity = quantity;
        }
    }
}