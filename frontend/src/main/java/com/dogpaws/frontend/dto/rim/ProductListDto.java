package com.dogpaws.frontend.dto.rim;

import lombok.Data;

@Data
public class ProductListDto {
    private Long productId;
    private String name;
    private Integer price;
    private String imageUrl;
    private String manufacturer;
    private String status;
}