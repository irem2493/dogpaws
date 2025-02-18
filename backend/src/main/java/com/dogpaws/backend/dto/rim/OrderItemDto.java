package com.dogpaws.backend.dto.rim;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderItemDto {
    @JsonProperty("order_item_id")
    private Long orderItemId;      
    
    @JsonProperty("ql_id")
    private String qlId;           
    
    @JsonProperty("product_id")
    private Long productId;        
    
    @JsonProperty("product_name")
    private String productName;    
    
    private int amount;            
    
    @JsonProperty("item_price")
    private int itemPrice;         
    
    private List<OrderItemOptionDto> options;
}