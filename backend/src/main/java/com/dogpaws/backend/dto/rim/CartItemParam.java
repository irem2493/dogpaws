package com.dogpaws.backend.dto.rim;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString  // 디버깅을 위해 추가
public class CartItemParam {
    private Long cartItemId;
    private Long productId;
    private int quantity;

    @Builder.Default
    private String username = null;
}